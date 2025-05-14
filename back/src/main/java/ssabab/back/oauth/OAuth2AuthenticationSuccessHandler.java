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
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.redirect-uri}")
    private String frontendRedirectUri;
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authToken.getPrincipal();
        String provider = authToken.getAuthorizedClientRegistrationId();  // "google" or "github"

        // Extract user info from the OAuth2User
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String loginName = oAuth2User.getAttribute("login");  // GitHub username
        String username = (name != null) ? name : (loginName != null ? loginName : "OAuthUser");
        if (email == null) {
            // If email is not provided, create a placeholder using provider info
            String idStr = oAuth2User.getAttribute("id").toString();
            if ("github".equalsIgnoreCase(provider) && loginName != null) {
                email = loginName + "@github.com";
            } else {
                email = provider + "_" + idStr + "@noemail.com";
            }
        }
        // Fetch profile image URL if available
        String imageUrl = oAuth2User.getAttribute("picture");
        if (imageUrl == null) {
            imageUrl = oAuth2User.getAttribute("avatar_url");
        }

        // Find existing account or create a new one for this social login
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        Account account;
        if (accountOpt.isPresent()) {
            account = accountOpt.get();
            // If this account was a local account, update its provider info (optional)
            if (account.getProvider() == null || "LOCAL".equals(account.getProvider())) {
                account.setProvider(provider.toUpperCase());
                account.setProviderId(oAuth2User.getAttribute("id").toString());
            }
        } else {
            account = new Account();
            account.setEmail(email);
            account.setUsername(username);
            account.setProvider(provider.toUpperCase());
            account.setProviderId(oAuth2User.getAttribute("id").toString());
            // Set a random password (not used for OAuth2 logins)
            String randomPwd = UUID.randomUUID().toString();
            account.setPassword(passwordEncoder.encode(randomPwd));
            account.setRole("USER");
            account.setActive(true);
        }
        if (imageUrl != null) {
            account.setProfileImgUrl(imageUrl);
        }
        // Save the account (this will insert new or update existing)
        account = accountRepository.save(account);

        // Generate JWT access token and refresh token
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        String accessToken = Jwts.builder()
                .setSubject(account.getEmail())
                .claim("userId", account.getUserId())
                .claim("role", account.getRole())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        String refreshToken = Jwts.builder()
                .setSubject(account.getEmail())
                .claim("userId", account.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        // Store refresh token in the account (server side)
        account.setRefreshToken(refreshToken);
        accountRepository.save(account);

        // Redirect to the frontend application with the access token as a URL parameter
        String redirectUrl = frontendRedirectUri;
        if (!redirectUrl.endsWith("/")) {
            redirectUrl += "/";
        }
        redirectUrl += "?token=" + accessToken;
        response.sendRedirect(redirectUrl);
    }
}
