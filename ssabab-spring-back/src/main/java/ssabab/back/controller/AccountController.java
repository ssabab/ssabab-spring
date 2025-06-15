
package ssabab.back.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.AccountDTO;
import ssabab.back.dto.SignupRequestDTO; // SignupRequestDTO는 사용자 입력 정보만 포함
import ssabab.back.dto.TokenDTO;
import ssabab.back.entity.Account;
import ssabab.back.jwt.JwtTokenProvider;
import ssabab.back.repository.AccountRepository;
import ssabab.back.service.AccountService;

import java.net.URI;
import java.time.LocalDate; // LocalDate 임포트
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.security.core.authority.AuthorityUtils; // AuthorityUtils 추가

/**
 * 회원 계정 관련 API 컨트롤러 - 로그인, 로그아웃, 회원가입, 프로필 조회/수정 담당
 */
@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인 시작 엔드포인트 (Google OAuth2 인증 페이지로 리디렉션)
     */
    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        URI redirectUri = URI.create("/oauth2/authorization/google");
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    /**
     * 회원가입 페이지 진입 엔드포인트 (소셜 로그인 후 신규 회원일 경우)
     * Google 로그인 정보 중 이메일을 전달하여 추가정보 입력을 안내
     * Authentication 객체 대신 쿼리 파라미터로 정보 받도록 수정 (SignupRequestDTO 필드명과 일관성 유지 필요)
     */
    @GetMapping("/signup")
    public ResponseEntity<Object> signupPage(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "provider", required = false) String provider,
            @RequestParam(value = "providerId", required = false) String providerId,
            @RequestParam(value = "profileImage", required = false) String profileImage,
            @RequestParam(value = "username", required = false) String username // OAuth2에서 넘어온 본명/이름
    ) {
        Map<String, Object> response = new HashMap<>();
        // 쿼리 파라미터로 최소한의 필수 정보 (email 또는 providerId)가 있는지 확인
        if (email != null || (provider != null && providerId != null)) {
            response.put("message", "추가 정보가 필요합니다. 회원가입을 완료해주세요.");
            // POST 요청에서 사용할 키 이름과 일치시킵니다.
            response.put("email", email);
            response.put("provider", provider);
            response.put("providerId", providerId);
            response.put("profileImage", profileImage);
            response.put("name", username); // 본명/이름
            response.put("requiredFields", new String[]{"username", "ssafyYear", "classNum", "ssafyRegion", "gender", "birthDate"});
            return ResponseEntity.ok(response);
        }
        // 필수 정보가 없는 경우 Unauthorized 처리
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", "소셜 로그인 정보가 부족합니다."));
    }

    /**
     * 회원가입 처리 엔드포인트 (POST 요청)
     * RequestBody Map에서 모든 회원가입 정보(OAuth2 + 사용자 입력)를 받습니다.
     * Authentication 객체 사용을 제거합니다.
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody Map<String, Object> signupRequest) {
        // OAuth2 정보 추출
        String provider = (String) signupRequest.get("provider");
        String email = (String) signupRequest.get("email");
        String providerId = (String) signupRequest.get("providerId");
        String profileImage = (String) signupRequest.get("profileImage");
        String name = (String) signupRequest.get("username");

        // 사용자 입력 정보 추출 (SignupRequestDTO 필드와 일치)
        String username = (String) signupRequest.get("username");
        String ssafyYear = (String) signupRequest.get("ssafyYear");
        String classNum = (String) signupRequest.get("classNum");
        String ssafyRegion = (String) signupRequest.get("ssafyRegion");
        String gender = (String) signupRequest.get("gender");
        String birthDateStr = (String) signupRequest.get("birthDate"); // 문자열로 받아서 파싱

        LocalDate birthDate = null;
        if (StringUtils.hasText(birthDateStr)) {
            try {
                birthDate = LocalDate.parse(birthDateStr);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Bad Request", "message", "유효하지 않은 생년월일 형식입니다. YYYY-MM-DD 형식이어야 합니다."));
            }
        }


        // 사용자 입력 정보의 최소 유효성 검증
        if (!StringUtils.hasText(username) || !StringUtils.hasText(ssafyYear) ||
                !StringUtils.hasText(classNum) || !StringUtils.hasText(ssafyRegion) ||
                !StringUtils.hasText(gender) || birthDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Bad Request", "message", "회원가입 필수 정보(닉네임, SSAFY 정보, 성별, 생년월일)가 누락되었습니다."));
        }

        // providerId가 없지만 이메일이 있다면 providerId를 이메일로 설정
        if (!StringUtils.hasText(providerId) && StringUtils.hasText(email)) {
            providerId = email;
        }
        // provider가 설정되지 않았다면 'google'로 기본값 설정
        if (!StringUtils.hasText(provider)) {
            provider = "google";
        }

        // SignupRequestDTO를 생성하여 AccountService로 전달 (사용자 입력 정보만 포함)
        SignupRequestDTO signupData = new SignupRequestDTO();
        signupData.setUsername(username);
        signupData.setSsafyYear(ssafyYear);
        signupData.setClassNum(classNum);
        signupData.setSsafyRegion(ssafyRegion);
        signupData.setGender(gender);
        signupData.setBirthDate(birthDate);

        try {
            // AccountService를 통해 새 사용자 등록
            // OAuth2 정보는 개별 파라미터로 전달, 사용자 입력 정보는 signupData DTO로 전달
            Account newAccount = accountService.registerNewAccount(
                    provider, providerId, email, signupData, profileImage
            );

            // 회원가입 성공 후 JWT 토큰 발급
            // 새로운 Account 객체에서 이메일과 기본 권한 (예: ROLE_USER)으로 Authentication 객체 생성
            Authentication jwtAuth = new UsernamePasswordAuthenticationToken(
                    newAccount.getEmail(), // principal (username, 여기서는 이메일)
                    null, // credentials (비밀번호 없음)
                    AuthorityUtils.createAuthorityList("USER") // 기본 역할 부여
            );
            String accessToken = jwtTokenProvider.createAccessToken(jwtAuth);
            String refreshToken = jwtTokenProvider.createRefreshToken(jwtAuth);

            // Refresh Token 저장
            newAccount.setRefreshToken(refreshToken);
            accountRepository.save(newAccount);

            TokenDTO tokenDTO = TokenDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenRemainingExpirySeconds())
                    .build();

            return ResponseEntity.ok(Map.of("message", "Signup successful", "token", tokenDTO));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "회원가입 실패: " + e.getMessage()));
        }
    }

    /**
     * 회원 정보 조회 엔드포인트 (로그인된 사용자의 프로필 반환)
     */
    @GetMapping("/info")
    public ResponseEntity<Object> getInfo(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = null;
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
        }

        if (email == null) {
            return ResponseEntity.status(400).body(Map.of("error", "Email not found in principal"));
        }

        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Account not found"));
        }

        AccountDTO profile = accountService.getProfile(accountOpt.get());
        return ResponseEntity.ok(profile);
    }

    /**
     * 회원 정보 수정 엔드포인트 (닉네임 등 프로필 변경)
     */
    @PutMapping("/update")
    public ResponseEntity<Object> updateInfo(Authentication authentication,
                                             @RequestBody SignupRequestDTO updateData) { // SignupRequestDTO 재사용 가능
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = null;
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
        }

        if (email == null) {
            return ResponseEntity.status(400).body(Map.of("error", "Email not found in principal"));
        }

        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Account not found"));
        }

        try {
            AccountDTO updatedProfile = accountService.updateProfile(accountOpt.get().getUserId(), updateData);
            return ResponseEntity.ok(Map.of("message", "User info updated", "profile", updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 로그아웃 엔드포인트 (JWT 기반에서는 클라이언트에서 토큰 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully. Please remove tokens from client storage."));
    }

    /**
     * Access Token 재발급 엔드포인트
     */
    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");

        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Refresh Token"));
        }

        try {
            Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();

            Optional<Account> accountOpt = accountRepository.findByEmail(email);
            if (accountOpt.isEmpty() || !accountOpt.get().getRefreshToken().equals(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Refresh Token or User Mismatch"));
            }

            String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

            TokenDTO tokenDTO = TokenDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenRemainingExpirySeconds())
                    .build();

            return ResponseEntity.ok(Map.of("message", "Access token refreshed", "token", tokenDTO));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh Token expired. Please log in again."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Token refresh failed: " + e.getMessage()));
        }
    }
}
