package ssabab.back.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import ssabab.back.dto.TokenDTO;
import ssabab.back.entity.Account;
import ssabab.back.jwt.JwtAuthenticationFilter;
import ssabab.back.jwt.JwtTokenProvider;
import ssabab.back.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 응답을 위해 추가
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.disable());

        // 세션 비활성화
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 요청 경로별 보안 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/account/signup",      // 회원가입 경로는 모두 허용
                        "/account/login",       // 로그인 페이지 경로 모두 허용
                        "/oauth2/**",           // OAuth2 인증 관련 경로 허용 (리디렉션 URI 등)
                        "/error",               // 에러 페이지 모두 허용
                        "/api/menu/**",          // 메뉴 조회 경로 허용 (로그인 X 케이스)
                        "/",                    // 메인 페이지 (로그인 X 케이스)
                        "/analysis/**"          // 분석 페이지 (로그인 X 케이스)
                ).permitAll()
                .anyRequest().authenticated()  // 그 외 나머지 요청은 인증 필요
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/account/login")                // 커스텀 로그인 페이지 경로
                .successHandler(authenticationSuccessHandler()) // 로그인 성공 핸들러
                .failureUrl("/account/login?error")         // 로그인 실패 시 리디렉션 경로
        );

        // 폼로그인 및 HTTP Basic 인증 비활성화 (소셜 로그인만 사용)
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // 기본 로그아웃 기능 비활성화 (로그아웃을 수동으로 처리 예정) - JWT 사용 시 서버 세션 불필요
        http.logout(logout -> logout.disable());

        // 인증 실패(미인증 접근) 시 예외 처리: 401 상태와 JSON 에러 메시지 반환
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

        // JWT 필터 추가
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

            Object principalObj = authentication.getPrincipal();

            if (principalObj instanceof OidcUser oidcUser) {
                email = oidcUser.getEmail();
                providerId = oidcUser.getSubject();
                profileImage = oidcUser.getPicture();
            } else if (principalObj instanceof OAuth2User oauth2User) {
                email = oauth2User.getAttribute("email");
                providerId = oauth2User.getName();
                profileImage = oauth2User.getAttribute("picture");
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
                // 이미 가입된 회원일 경우: JWT 토큰 발급 후 응답
                Account existingAccount = accountOpt.get();
                Authentication jwtAuth = new UsernamePasswordAuthenticationToken(
                        existingAccount.getEmail(), null, authentication.getAuthorities()
                );
                String accessToken = jwtTokenProvider.createAccessToken(jwtAuth);
                String refreshToken = jwtTokenProvider.createRefreshToken(jwtAuth);

                // Refresh Token 저장 (필요하다면 Account 엔티티에 필드를 추가하고 저장)
                existingAccount.setRefreshToken(refreshToken);
                accountRepository.save(existingAccount);

                TokenDTO tokenDTO = TokenDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(jwtTokenProvider.getAccessTokenRemainingExpirySeconds())
                        .build();

                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json;charset=UTF-8");
                try (PrintWriter writer = response.getWriter()) {
                    new ObjectMapper().writeValue(writer, tokenDTO);
                }
            } else {
                // 신규 회원일 경우: 회원가입 페이지로 리디렉션 (프론트엔드에서 OAuth2 정보 받아서 추가 정보 입력)
                response.sendRedirect("/account/signup");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}