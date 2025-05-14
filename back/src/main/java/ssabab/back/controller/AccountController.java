package ssabab.back.controller;

import ssabab.back.dto.AccountDTO;
import ssabab.back.dto.AuthResponse;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;
import ssabab.back.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/back")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    // User signup (register). On success, return an access token (and user info)
    @PostMapping("/save")
    public ResponseEntity<AuthResponse> register(@RequestBody AccountDTO accountDTO) {
        Account account = accountService.save(accountDTO);
        // Generate JWT access & refresh tokens
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
        // Store refresh token on server (in DB) and do NOT send it to the client
        account.setRefreshToken(refreshToken);
        accountRepository.save(account);
        // Respond with access token and basic user info
        AuthResponse response = new AuthResponse(accessToken, account.getEmail(), account.getUsername());
        return ResponseEntity.ok(response);
    }

    // Local login. On success, return a new access token (refresh token stored server-side)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AccountDTO accountDTO) {
        Account account = accountService.login(accountDTO);
        if (account == null) {
            // Invalid credentials
            return ResponseEntity.status(401).build();
        }
        // Generate JWT access & refresh tokens
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
        // Update refresh token in database
        account.setRefreshToken(refreshToken);
        accountRepository.save(account);
        // Return access token and user info (email, username)
        AuthResponse response = new AuthResponse(accessToken, account.getEmail(), account.getUsername());
        return ResponseEntity.ok(response);
    }

    // Exchange an expired (or about-to-expire) access token for a new one, using the server-side refresh token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        String email;
        INTGER userId;
        try {
            // Parse claims (if token is expired, this will throw ExpiredJwtException)
            var claims = Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build()
                         .parseClaimsJws(token).getBody();
            email = claims.getSubject();
            userId = claims.get("userId", Integer.class);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token expired - extract claims from exception
            email = e.getClaims().getSubject();
            userId = e.getClaims().get("userId", Integer.class);
        } catch (Exception e) {
            // Invalid token
            return ResponseEntity.status(401).build();
        }
        // Retrieve account and verify stored refresh token
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        Account account = accountOpt.get();
        if (account.getRefreshToken() == null) {
            return ResponseEntity.status(401).build();
        }
        // Validate the refresh token from DB
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes())
                .build().parseClaimsJws(account.getRefreshToken());
        } catch (Exception e) {
            // Stored refresh token is invalid or expired
            return ResponseEntity.status(401).build();
        }
        // Generate a new access token (do not issue a new refresh token here)
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        String newAccessToken = Jwts.builder()
                .setSubject(account.getEmail())
                .claim("userId", account.getUserId())
                .claim("role", account.getRole())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        AuthResponse response = new AuthResponse(newAccessToken, account.getEmail(), account.getUsername());
        return ResponseEntity.ok(response);
    }

    // List all accounts (protected – consider requiring admin role)
    @GetMapping("/accounts")
    public List<AccountDTO> findAllAccounts() {
        return accountService.findAll();
    }

    // Get account details by userId
    @GetMapping("/accounts/{userId}")
    public ResponseEntity<AccountDTO> getAccountDetail(@PathVariable Integer userId) {
        AccountDTO accountDTO = accountService.findByUserId(userId);
        return (accountDTO != null) ? ResponseEntity.ok(accountDTO)
                                    : ResponseEntity.notFound().build();
    }

    // Update an account (e.g., profile update)
    @PutMapping("/accounts/{userId}")
    public ResponseEntity<Void> updateAccount(@PathVariable Integer userId, @RequestBody AccountDTO accountDTO) {
        accountDTO.setUserId(userId);
        accountService.update(accountDTO);
        return ResponseEntity.ok().build();
    }

    // User logout: invalidate the refresh token on the server
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = null;
            try {
                email = Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes())
                          .build().parseClaimsJws(token).getBody().getSubject();
            } catch (Exception ignored) {}
            if (email != null) {
                accountRepository.findByEmail(email).ifPresent(account -> {
                    account.setRefreshToken(null);
                    accountRepository.save(account);
                });
            }
        }
        return ResponseEntity.ok().build();
    }

    // Email availability check (for signup form AJAX)
    @GetMapping("/email-check")
    public ResponseEntity<String> emailCheck(@RequestParam("email") String email) {
        String result = accountService.emailCheck(email);
        // If email is taken, return empty string (or error code), otherwise "ok"
        return ResponseEntity.ok(result != null ? result : "");
    }
}



// @RestController
// @RequestMapping("/account")
// @RequiredArgsConstructor
// public class AccountController {

//     private final AccountService accountService;

//     /* ---------- 회원가입 ---------- */
//     @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
//     public ResponseEntity<?> signup(@RequestBody AccountDTO dto) {
//         accountService.save(dto);
//         return ResponseEntity.ok(Map.of("message", "SIGNUP_OK"));
//     }

//     /* ---------- 로그인 ---------- */
//     @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
//     public ResponseEntity<?> login(@RequestBody AccountDTO dto, HttpSession session) {
//         AccountDTO user = accountService.login(dto);
//         if (user != null) {
//             session.setAttribute("loginEmail", user.getEmail());       // 세션 유지
//             return ResponseEntity.ok(Map.of("userId", user.getUserId())); // ← user_id만 반환
//         }
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                 .body(Map.of("error", "LOGIN_FAIL"));
//     }

//     /* ---------- 로그아웃 ---------- */
//     @PostMapping(value = "/logout", produces = "application/json")
//     public ResponseEntity<?> logout(HttpSession session) {
//         session.invalidate();
//         return ResponseEntity.ok(Map.of("message", "LOGOUT_OK"));
//     }

//     /* ---------- 회원 전체 목록 ---------- */
//     @GetMapping(produces = "application/json")          // GET /account
//     public List<AccountDTO> findAll() {
//         return accountService.findAll();
//     }

//     /* ---------- 단일 회원 조회 ---------- */
//     @GetMapping(value = "/{userId}", produces = "application/json")
//     public ResponseEntity<?> findById(@PathVariable Integer userId) {
//         AccountDTO dto = accountService.findByuserId(userId);
//         return dto != null ? ResponseEntity.ok(dto)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND)
//                 .body(Map.of("error", "NOT_FOUND"));
//     }

//     /* ---------- 이메일 중복 검사 ---------- */
//     @PostMapping(value = "/email-check", consumes = "application/json", produces = "application/json")
//     public ResponseEntity<?> emailCheck(@RequestBody String accountEmail) {
//         boolean duplicate = accountService.emailExists(accountEmail.replace("\"", ""));
//         return ResponseEntity.ok(Map.of("duplicate", duplicate));
//     }
// }
