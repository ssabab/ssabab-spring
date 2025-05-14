package ssabab.back.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;  // 소셜 로그인 후 리디렉션할 프론트엔드 URI

    private final AccountRepository accountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String provider = authToken.getAuthorizedClientRegistrationId();  // "google", "github" 등
        OAuth2User oAuth2User = authToken.getPrincipal();

        // 소셜 프로바이더별로 사용자 정보 추출
        String email = null;
        String name = null;
        String avatarUrl = null;
        if ("google".equals(provider)) {
            // 구글 기본 OAuth2 프로필
            email = (String) oAuth2User.getAttributes().get("email");
            name  = (String) oAuth2User.getAttributes().get("name");
            avatarUrl = (String) oAuth2User.getAttributes().get("picture");
        } else if ("github".equals(provider)) {
            // Kakao의 사용자 정보는 kakao_account와 properties에 중첩되어 있음
            Map<String, Object> githubAccount = (Map<String, Object>) oAuth2User.getAttributes().get("github_account");
            Map<String, Object> githubProps   = (Map<String, Object>) oAuth2User.getAttributes().get("properties");
            if (githubAccount != null) {
                email = (String) githubAccount.get("email");
            }
            if (githubProps != null) {
                name = (String) githubProps.get("nickname");
                avatarUrl = (String) githubProps.get("profile_image");
            }
            if (email == null) {
                // 이메일 제공에 동의하지 않은 경우: 카카오 고유 ID를 이용하여 dummy 이메일 생성
                Object githubId = oAuth2User.getAttributes().get("id");
                email = "github" + githubId + "@github.com";
            }
            if (name == null) {
                name = "깃헙사용자";  // 이름 정보가 없으면 기본값 설정
            }
        } else {
            // 기타(provider: kakao 등) 기본 처리
            email = (String) oAuth2User.getAttributes().get("email");
            if (email == null) {
                // GitHub의 경우 public email이 없을 수 있음 -> 로그인 아이디로 이메일 조합
                String login = (String) oAuth2User.getAttributes().get("login");
                email = login + "@github.com";
            }
            name = (String) oAuth2User.getAttributes().getOrDefault("name", oAuth2User.getAttributes().get("login"));
            avatarUrl = (String) oAuth2User.getAttributes().get("avatar_url");
        }

        // Account 조회 또는 신규 생성
        Account account = accountRepository.findByEmail(email).orElseGet(Account::new);
        boolean isNewAccount = (account.getUserId() == null);
        if (isNewAccount) {
            // 신규 회원인 경우 Account 초기화
            account.setEmail(email);
            // 소셜 계정이므로 임시 비밀번호 발급 (bcrypt 해시 저장)
            String randomPwd = UUID.randomUUID().toString();
            account.setPassword(passwordEncoder.encode(randomPwd));
            account.setRole("ROLE_USER");
            account.setActive(true);
        }
        // 소셜 로그인 시마다 최신 프로필 정보로 업데이트
        account.setUsername(name);
        account.setProvider(provider);
        account.setProviderId(String.valueOf(oAuth2User.getAttribute("id")));
        account.setProfileImgUrl(avatarUrl);

        // JWT 토큰 생성 (Access/Refresh) 및 Account 갱신
        String accessToken = jwtTokenProvider.generateToken(account.getEmail(), account.getRole(), account.getUserId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(account.getEmail());
        account.setRefreshToken(refreshToken);
        accountRepository.save(account);

        // 프론트엔드로 리디렉션 (Access Token 전달, Refresh Token은 전달하지 않음)
        String redirectTarget = redirectUri + "?accessToken=" + accessToken;
        response.sendRedirect(redirectTarget);
    }
}

