package ssabab.back.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter to authenticate requests via JWT tokens.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Key signingKey;

    public JwtAuthenticationFilter(String secret) {
        // Initialize the signing key for HMAC using the JWT secret
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // No JWT provided, continue without setting authentication
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.substring(7);
        try {
            // Validate and parse token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // Extract user identity and role from token claims
            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (role != null) {
                // Prefix role with "ROLE_" for Spring Security authorities
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            // Create Authentication and set in security context
            Authentication auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            // Invalid or expired token – authentication remains null
            SecurityContextHolder.clearContext();
        }
        // Proceed with the next filter
        filterChain.doFilter(request, response);
    }
}
