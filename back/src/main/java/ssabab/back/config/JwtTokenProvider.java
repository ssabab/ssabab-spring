package ssabab.back.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;
    private Key signingKey;
    
    @PostConstruct
    public void init() {
        // 시크릿 키를 HMAC SHA 키 객체로 변환
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    // Access Token 생성 (사용자 이메일, 권한, 사용자ID 포함)
    public String generateToken(String email, String role, Integer userId) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .claim("role", role);
        if (userId != null) {
            builder.claim("userId", userId);
        }
        return builder
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // Refresh Token 생성 (긴 만료기간, 최소한의 정보만 포함)
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // JWT에서 클레임 추출
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build()
                .parseClaimsJws(token).getBody();
    }
}