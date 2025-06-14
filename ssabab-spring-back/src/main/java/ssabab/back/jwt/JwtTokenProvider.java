package ssabab.back.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiry; // 초 단위

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry; // 초 단위

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Access Token 생성
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenExpiry);
    }

    // Refresh Token 생성
    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExpiry);
    }

    // JWT 토큰 생성
    private String createToken(Authentication authentication, long expirySeconds) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date validity = new Date(now.getTime() + expirySeconds * 1000); // 밀리초 단위로 변환

        return Jwts.builder()
                .setSubject(authentication.getName()) // 이메일 또는 사용자 ID
                .claim("auth", authorities) // 권한 정보
                .setIssuedAt(now) // 발행 시간
                .setExpiration(validity) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // "잘못된 JWT 서명입니다."
        } catch (ExpiredJwtException e) {
            // "만료된 JWT 토큰입니다."
        } catch (UnsupportedJwtException e) {
            // "지원되지 않는 JWT 토큰입니다."
        } catch (IllegalArgumentException e) {
            // "JWT 토큰이 잘못되었습니다."
        }
        return false;
    }

    // Access Token의 남은 만료 시간 (초)
    public Long getAccessTokenRemainingExpirySeconds() {
        return accessTokenExpiry;
    }
}