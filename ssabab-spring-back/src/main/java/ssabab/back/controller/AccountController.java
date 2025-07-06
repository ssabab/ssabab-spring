// controller.AccountController
package ssabab.back.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // ResponseEntity는 다른 메서드에서 사용되므로 import 유지
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

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.security.core.authority.AuthorityUtils;

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
    @Value("${front.url}")
    private String FRONTEND_APP_BASE_URL;


    /**
     * 로그인 시작 엔드포인트 (Google OAuth2 인증 페이지로 리다이렉션)
     */
    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        URI redirectUri = URI.create("/oauth2/authorization/google");
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    /**
     * 회원가입 페이지 진입 엔드포인트 (소셜 로그인 후 신규 회원일 경우)
     */
    @GetMapping("/signup")
    public ResponseEntity<Object> signupPage(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "provider", required = false) String provider,
            @RequestParam(value = "providerId", required = false) String providerId,
            @RequestParam(value = "profileImage", required = false) String profileImage,
            @RequestParam(value = "username", required = false) String username
    ) {
        Map<String, Object> response = new HashMap<>();

        if (email != null || (provider != null && providerId != null)) {
            response.put("message", "추가 정보가 필요합니다. 회원가입을 완료해주세요.");
            response.put("email", email);
            response.put("provider", provider);
            response.put("providerId", providerId);
            response.put("profileImage", profileImage);
            response.put("name", username);
            response.put("requiredFields", new String[]{"username", "ssafyYear", "classNum", "ssafyRegion", "gender", "birthDate"});

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", "소셜 로그인 정보가 부족합니다. 다시 로그인해주세요."));
    }

    /**
     * 회원가입 처리 엔드포인트 (POST 요청)
     * RequestBody Map에서 모든 회원가입 정보(OAuth2 + 사용자 입력)를 받습니다.
     * Authentication 객체 사용은 제거됩니다.
     */
    @PostMapping("/signup")
    public void signup(HttpServletResponse response, // 변경: 반환 타입을 void로 변경했으므로, 메서드가 명시적으로 ResponseEntity를 반환하지 않음
                       @RequestBody Map<String, Object> signupRequest) throws IOException { // 변경: throws IOException 유지
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
        String birthDateStr = (String) signupRequest.get("birthDate");

        LocalDate birthDate = null;
        if (StringUtils.hasText(birthDateStr)) {
            try {
                birthDate = LocalDate.parse(birthDateStr);
            } catch (Exception e) {
                response.sendRedirect(FRONTEND_APP_BASE_URL + "/signup?error=invalid_birthdate");
                return; // 변경: 리다이렉트 후 명시적으로 메서드 종료
            }
        }

        if (!StringUtils.hasText(email) && (!StringUtils.hasText(provider) || !StringUtils.hasText(providerId))) {
            response.sendRedirect(FRONTEND_APP_BASE_URL + "/signup?error=oauth_info_missing");
            return; // 변경: 리다이렉트 후 명시적으로 메서드 종료
        }
        if (!StringUtils.hasText(username) || !StringUtils.hasText(ssafyYear) ||
                !StringUtils.hasText(classNum) || !StringUtils.hasText(ssafyRegion) ||
                !StringUtils.hasText(gender) || birthDate == null) {
            response.sendRedirect(FRONTEND_APP_BASE_URL + "/signup?error=required_fields_missing");
            return; // 변경: 리다이렉트 후 명시적으로 메서드 종료
        }

        if (!StringUtils.hasText(providerId) && StringUtils.hasText(email)) {
            providerId = email;
        }
        if (!StringUtils.hasText(provider)) {
            provider = "google";
        }

        SignupRequestDTO signupData = new SignupRequestDTO();
        signupData.setUsername(username);
        signupData.setSsafyYear(ssafyYear);
        signupData.setClassNum(classNum);
        signupData.setSsafyRegion(ssafyRegion);
        signupData.setGender(gender);
        signupData.setBirthDate(birthDate);

        try {
            Account newAccount = accountService.registerNewAccount(
                    provider, providerId, email, signupData, profileImage
            );

            Authentication jwtAuth = new UsernamePasswordAuthenticationToken(
                    newAccount.getEmail(),
                    null,
                    AuthorityUtils.createAuthorityList("USER")
            );
            String accessToken = jwtTokenProvider.createAccessToken(jwtAuth);
            String refreshToken = jwtTokenProvider.createRefreshToken(jwtAuth);

            newAccount.setRefreshToken(refreshToken);
            accountRepository.save(newAccount);

            String redirectUrl = String.format("%s/?accessToken=%s",
                    FRONTEND_APP_BASE_URL+"/ssabab",
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8.toString()),
                    jwtTokenProvider.getAccessTokenRemainingExpirySeconds());

            response.sendRedirect(redirectUrl); // 브라우저를 이 URL로 리다이렉트
            return; // 변경: 리다이렉트 후 명시적으로 메서드 종료

        } catch (IllegalStateException e) { // 계정 중복 등
            response.sendRedirect(FRONTEND_APP_BASE_URL + "/signup?error=account_exists&message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.toString()));
            return; // 변경: 리다이렉트 후 명시적으로 메서드 종료
        } catch (Exception e) {
            response.sendRedirect(FRONTEND_APP_BASE_URL + "/signup?error=signup_failed&message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8.toString()));
            return; // 변경: 리다이렉트 후 명시적으로 메서드 종료
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
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully. Please remove tokens from client storage."));
    }

    /**
     * 유저 네임 중복 체크 로직
     */
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        boolean exists = accountRepository.findByUsername(username).isPresent();
        return ResponseEntity.ok(exists);
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