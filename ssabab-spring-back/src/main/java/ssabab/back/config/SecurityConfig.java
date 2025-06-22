package ssabab.back.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import ssabab.back.dto.TokenDTO; // 이제 TokenDTO는 응답 본문에 직접 쓰이지 않으므로, import는 불필요할 수 있음
import ssabab.back.entity.Account;
import ssabab.back.jwt.JwtAuthenticationFilter;
import ssabab.back.jwt.JwtTokenProvider;
import ssabab.back.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper; // 이제 ObjectMapper는 사용되지 않음
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpirySeconds;

    private static final String FRONTEND_APP_BASE_URL = "http://localhost:3000"; // 앱의 기본 경로
    private static final String FRONTEND_SIGNUP_BASE_URL = "http://localhost:3000"; // 회원가입 페이지 기본 경로

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/account/signup",
                        "/account/login",
                        "/oauth2/**",
                        "/error",
                        "/api/menu/**",
                        "/",
                        "/api/dashboard/monthly/**",
                        "/account/refresh"
                ).permitAll()
                .anyRequest().authenticated()
        );

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/account/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/account/login?error")
        );

        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());
        http.logout(logout -> logout.disable());

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response,
                                         org.springframework.security.core.AuthenticationException authException) throws IOException {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setContentType("application/json;charset=UTF-8");
                        try (PrintWriter writer = response.getWriter()) {
                            writer.print("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
                        }
                    }
                })
        );

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String provider = "google";
            String email = null;
            String providerId = null;
            String profileImage = null;
            String oauth2Name = null;

            Object principalObj = authentication.getPrincipal();

            if (principalObj instanceof OidcUser oidcUser) {
                email = oidcUser.getEmail();
                providerId = oidcUser.getSubject();
                profileImage = oidcUser.getPicture();
                oauth2Name = oidcUser.getFullName();
            } else if (principalObj instanceof OAuth2User oauth2User) {
                email = oauth2User.getAttribute("email");
                providerId = oauth2User.getName();
                profileImage = oauth2User.getAttribute("picture");
                oauth2Name = oauth2User.getAttribute("name");
            }

            if (providerId == null && email != null) {
                providerId = email;
            }

            Optional<Account> accountOpt = Optional.empty();
            if (providerId != null) {
                accountOpt = accountRepository.findByProviderAndProviderId(provider, providerId);
            } else if (email != null) {
                accountOpt = accountRepository.findByEmail(email);
            }

            if (accountOpt.isPresent()) {
                // 변경: 기존 회원: JWT 토큰 발급 후 Access Token을 URL 쿼리 파라미터로 리다이렉트
                Account existingAccount = accountOpt.get();
                Authentication jwtAuth = new UsernamePasswordAuthenticationToken(
                        existingAccount.getEmail(), null, authentication.getAuthorities()
                );
                String accessToken = jwtTokenProvider.createAccessToken(jwtAuth);
                String refreshToken = jwtTokenProvider.createRefreshToken(jwtAuth);

                existingAccount.setRefreshToken(refreshToken);
                accountRepository.save(existingAccount);


                // Access Token을 URL 쿼리 파라미터로 포함하여 프론트엔드 앱의 기본 경로로 리다이렉트
                // 프론트엔드는 이 URL을 파싱하여 Access Token을 localStorage에 저장해야 합니다.
                String redirectUrl = String.format("%s/?accessToken=%s&refreshToken=%s", // 변경: expiresIn도 추가
                        FRONTEND_APP_BASE_URL + "/ssabab", // 변경: 정확한 프론트엔드 앱의 시작 경로 (Next.js 앱의 기준 경로)
                        URLEncoder.encode(accessToken, StandardCharsets.UTF_8.toString()));
//

                response.sendRedirect(redirectUrl); // 브라우저를 이 URL로 리다이렉트

            } else {
                // 신규 회원: OAuth2 정보를 쿼리 파라미터로 포함하여 프론트엔드 회원가입 페이지로 리다이렉트
                StringBuilder redirectUrlBuilder = new StringBuilder(FRONTEND_SIGNUP_BASE_URL + "/signup?"); // 변경: /signup 앞에 FRONTEND_SIGNUP_BASE_URL (http://localhost:3000)

                if (email != null) {
                    redirectUrlBuilder.append("email=").append(URLEncoder.encode(email, StandardCharsets.UTF_8.toString()));
                }
                if (provider != null) {
                    if (redirectUrlBuilder.length() > (FRONTEND_SIGNUP_BASE_URL + "/signup?").length()) redirectUrlBuilder.append("&");
                    redirectUrlBuilder.append("provider=").append(URLEncoder.encode(provider, StandardCharsets.UTF_8.toString()));
                }
                if (providerId != null) {
                    if (redirectUrlBuilder.length() > (FRONTEND_SIGNUP_BASE_URL + "/signup?").length()) redirectUrlBuilder.append("&");
                    redirectUrlBuilder.append("providerId=").append(URLEncoder.encode(providerId, StandardCharsets.UTF_8.toString()));
                }
                if (profileImage != null) {
                    if (redirectUrlBuilder.length() > (FRONTEND_SIGNUP_BASE_URL + "/signup?").length()) redirectUrlBuilder.append("&");
                    redirectUrlBuilder.append("profileImage=").append(URLEncoder.encode(profileImage, StandardCharsets.UTF_8.toString()));
                }
                if (oauth2Name != null) {
                    if (redirectUrlBuilder.length() > (FRONTEND_SIGNUP_BASE_URL + "/signup?").length()) redirectUrlBuilder.append("&");
                    redirectUrlBuilder.append("name=").append(URLEncoder.encode(oauth2Name, StandardCharsets.UTF_8.toString())); // 프론트엔드에서 name 쿼리 파라미터로 받도록
                }

                response.sendRedirect(redirectUrlBuilder.toString());
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://ssabab.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}