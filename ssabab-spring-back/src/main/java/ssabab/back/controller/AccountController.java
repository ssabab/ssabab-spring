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
import ssabab.back.dto.SignupRequestDTO;
import ssabab.back.dto.TokenDTO;
import ssabab.back.entity.Account;
import ssabab.back.jwt.JwtTokenProvider;
import ssabab.back.repository.AccountRepository;
import ssabab.back.service.AccountService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        // 이 엔드포인트는 실제로는 프론트엔드에서 직접 /oauth2/authorization/google로 리디렉션하는 데 사용됩니다.
        // 백엔드에서 강제 리디렉션하는 것은 프론트엔드의 동작 방식에 따라 달라질 수 있습니다.
        // 현재 SecurityConfig에서 loginPage 설정으로 인해 이 URL로 접근 시 OAuth2 흐름이 시작됩니다.
        URI redirectUri = URI.create("/oauth2/authorization/google");
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    /**
     * 회원가입 페이지 진입 엔드포인트 (소셜 로그인 후 신규 회원일 경우)
     * Google 로그인 정보 중 이메일을 전달하여 추가정보 입력을 안내
     */
    @GetMapping("/signup")
    public ResponseEntity<Object> signupPage(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User principal) {
            String email = principal.getAttribute("email");
            response.put("message", "추가 정보가 필요합니다. 회원가입을 완료해주세요.");
            response.put("email", email);
            response.put("requiredFields", new String[]{"username", "ssafyYear", "classNum", "ssafyRegion", "gender", "age"});
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", "소셜 로그인 정보가 없습니다."));
    }

    /**
     * 회원가입 처리 엔드포인트 (POST 요청)
     * Google OAuth2 인증 정보 + 추가입력 정보를 받아 Account 생성
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(Authentication authentication,
                                         @RequestBody SignupRequestDTO signupData) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User principal)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", "유효한 소셜 로그인 정보가 없습니다."));
        }

        String provider = "google"; // Google OAuth2만 지원한다고 가정
        String email = null;
        String providerId = null;
        String profileImage = null;

        if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();
            providerId = oidcUser.getSubject();
            profileImage = oidcUser.getPicture();
        } else {
            email = principal.getAttribute("email");
            providerId = principal.getName();
            profileImage = principal.getAttribute("picture");
        }

        if (providerId == null && email != null) {
            providerId = email;
        }

        try {
            // AccountService를 통해 새 사용자 등록
            Account newAccount = accountService.registerNewAccount(provider, providerId, email, signupData, profileImage);

            // 회원가입 성공 후 JWT 토큰 발급
            Authentication jwtAuth = new UsernamePasswordAuthenticationToken(
                    newAccount.getEmail(), null, authentication.getAuthorities()
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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

        // JWT 인증 후 UserDetails 타입으로 principal이 설정됨
        String email = null;
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // UserDetails의 username은 여기서는 email
        } else if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            // OAuth2UserPrincipal이 직접 넘어오는 경우 (테스트 환경 등)
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
                                             @RequestBody SignupRequestDTO updateData) {
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
        // JWT 기반에서는 서버 측에서 세션을 무효화할 필요가 없음.
        // 클라이언트에서 토큰을 삭제하도록 안내하는 메시지를 반환
        SecurityContextHolder.clearContext(); // Spring Security Context 초기화
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

            // 새로운 Access Token 발급
            String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

            TokenDTO tokenDTO = TokenDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // Refresh Token은 그대로 사용
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