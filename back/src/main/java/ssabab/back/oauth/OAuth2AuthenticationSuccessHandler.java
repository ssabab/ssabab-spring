package ssabab.back.oauth;

import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ssabab.back.config.JwtTokenProvider;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AccountRepository repo;
    private final JwtTokenProvider jwt;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();   // google | github
        OAuth2User user  = token.getPrincipal();

        String email, name, avatar;
        if ("google".equals(provider)) {
            email  = (String) user.getAttributes().get("email");
            name   = (String) user.getAttributes().get("name");
            avatar = (String) user.getAttributes().get("picture");
        } else {                                   // github
            email  = (String) user.getAttributes().get("email");
            if (email==null) email = user.getAttribute("login")+"@github.com";
            name   = Optional.ofNullable(user.getAttribute("name"))
                              .orElse(user.getAttribute("login"));
            avatar = user.getAttribute("avatar_url");
        }

        Account acc = repo.findByEmail(email).orElseGet(Account::new);
        if (acc.getUserId()==null) {           // 신규
            acc.setEmail(email);
            acc.setPassword(UUID.randomUUID().toString());
            acc.setRole("ROLE_USER");
            acc.setActive(true);
        }
        acc.setUsername(name);
        acc.setProvider(provider);
        acc.setProviderId(String.valueOf(user.getAttribute("id")));
        acc.setProfileImgUrl(avatar);

        String access  = jwt.generateToken(acc.getEmail(), acc.getRole());
        String refresh = jwt.generateRefreshToken(acc.getEmail());
        acc.setRefreshToken(refresh);
        repo.save(acc);

        String redirect = redirectUri+"?accessToken="+access+"&refreshToken="+refresh;
        response.sendRedirect(redirect);
    }
}
