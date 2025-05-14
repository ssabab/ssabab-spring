package ssabab.back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletResponse;
import ssabab.back.oauth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF as we're using stateless REST API
        http.csrf().disable();
        // No HTTP session – use JWT for authentication
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Define URL-based access rules
        http.authorizeHttpRequests()
            .requestMatchers("/back/login", "/back/save", "/back/refresh", "/back/email-check", "/oauth2/**", "/login/oauth2/**")
            .permitAll()
            .anyRequest().authenticated();
        // Return 401/403 for unauthorized/forbidden instead of default redirect
        http.exceptionHandling()
            .authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .accessDeniedHandler((req, res, ex) -> res.sendError(HttpServletResponse.SC_FORBIDDEN));
        // Add JWT authentication filter before the default security filters
        http.addFilterBefore(new JwtAuthenticationFilter(jwtSecret), UsernamePasswordAuthenticationFilter.class);
        // OAuth2 login configuration: use custom success handler to generate JWT on social login
        http.oauth2Login().successHandler(oAuth2LoginSuccessHandler);
        return http.build();
    }
}
