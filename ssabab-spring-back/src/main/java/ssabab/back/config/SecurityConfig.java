// config.SecurityConfig.java
package ssabab.back.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import ssabab.back.entity.Account;
import ssabab.back.jwt.JwtAuthenticationFilter;
import ssabab.back.jwt.JwtTokenProvider;
import ssabab.back.repository.AccountRepository;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

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
    @Value("${front.url}")
    private  String FRONTEND_APP_BASE_URL;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                        "/account/signup",
                        "/account/check-username",
                        "/account/login",
                        "/oauth2/**",
                        "/error",
                        "/api/menu/**",
                        "/",
                        "/api/analysis/monthly", // 월간 분석 API는 모두 허용
                        "/account/refresh",
                        "/swagger-ui.html",         // Swagger UI 메인 페이지
                        "/swagger-ui/**",           // Swagger UI의 모든 하위 정적 리소스 (CSS, JS 등)
                        "/v3/api-docs/**",          // OpenAPI JSON/YAML 문서
                        "/webjars/**"

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
                Account existingAccount = accountOpt.get();
                Authentication jwtAuth = new UsernamePasswordAuthenticationToken(
                        existingAccount.getEmail(), null, authentication.getAuthorities()
                );
                String accessToken = jwtTokenProvider.createAccessToken(jwtAuth);
                String refreshToken = jwtTokenProvider.createRefreshToken(jwtAuth);

                existingAccount.setRefreshToken(refreshToken);
                accountRepository.save(existingAccount);

                String redirectUrl = String.format("%s/?accessToken=%s&refreshToken=%s",
                        FRONTEND_APP_BASE_URL + "/ssabab",
                        URLEncoder.encode(accessToken, StandardCharsets.UTF_8.toString()),
                        URLEncoder.encode(refreshToken, StandardCharsets.UTF_8.toString()));

                response.sendRedirect(redirectUrl);

            } else {
                StringBuilder redirectUrlBuilder = new StringBuilder(FRONTEND_APP_BASE_URL + "/signup?");

                if (email != null) {
                    redirectUrlBuilder.append("email=").append(URLEncoder.encode(email, StandardCharsets.UTF_8.toString()));
                }
                if (provider != null) {
                    if (redirectUrlBuilder.length() > (FRONTEND_APP_BASE_URL + "/signup?").length()) redirectUrlBuilder.append("&");
                    redirectUrlBuilder.append("provider=").append(URLEncoder.encode(provider, StandardCharsets.UTF_8.toString()));
                }
                if (providerId != null) {
                    if (redirectUrlBuilder.length() > (FRONTEND_APP_BASE_URL + "/signup?").length()) redirectUrlBuilder.append("&");
                    redirectUrlBuilder.append("providerId=").append(URLEncoder.encode(providerId, StandardCharsets.UTF_8.toString()));
                }
                if (profileImage != null) {
                    if (redirectUrlBuilder.length() > (FRONTEND_APP_BASE_URL + "/signup?").length()) redirectUrlBuilder.append("&");
                    redirectUrlBuilder.append("profileImage=").append(URLEncoder.encode(profileImage, StandardCharsets.UTF_8.toString()));
                }
                if (oauth2Name != null) {
                    if (redirectUrlBuilder.length() > (FRONTEND_APP_BASE_URL + "/signup?").length()) redirectUrlBuilder.append("&");
                    redirectUrlBuilder.append("name=").append(URLEncoder.encode(oauth2Name, StandardCharsets.UTF_8.toString()));
                }

                response.sendRedirect(redirectUrlBuilder.toString());
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://ssabab.com","http://localhost:3000","http://ssaback.com","https://ssabab.com","https://ssaback.com","https://localhost:3000","https://www.ssabab.com","https://www.ssaback.com"));
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