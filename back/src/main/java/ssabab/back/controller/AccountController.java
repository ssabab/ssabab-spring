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
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String jwtSecret; 
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;
    
    // 회원가입 (새 계정 등록)
    @PostMapping("/save")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = accountService.save(accountDTO);
            // 회원가입 성공 -> JWT 생성하여 반환 (자동 로그인 처리)
            String accessToken = jwtTokenProvider.generateToken(account.getEmail(), account.getRole(), account.getUserId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(account.getEmail());
            // Refresh Token 저장
            account.setRefreshToken(refreshToken);
            accountRepository.save(account);
            // 응답 객체 생성 (액세스 토큰과 사용자 정보 전달)
            AuthResponse response = new AuthResponse(accessToken, account.getEmail(), account.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            // 이메일 중복 등으로 인한 회원가입 불가
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    // 로그인 (이메일/비밀번호 인증)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AccountDTO accountDTO) {
        try {
            Account account = accountService.login(accountDTO);
            // 로그인 성공 -> JWT 생성
            String accessToken = jwtTokenProvider.generateToken(account.getEmail(), account.getRole(), account.getUserId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(account.getEmail());
            // Refresh Token 저장
            account.setRefreshToken(refreshToken);
            accountRepository.save(account);
            // 응답 반환 (액세스 토큰 + 사용자 이메일/이름)
            AuthResponse response = new AuthResponse(accessToken, account.getEmail(), account.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 이메일 없음 또는 비밀번호 불일치
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (IllegalStateException e) {
            // 비활성화 등 기타 로그인 불가 상태
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    // 이메일 중복 체크
    @GetMapping("/email-check")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean available = accountRepository.findByEmail(email).isEmpty();
        return ResponseEntity.ok(available);
    }

    // 로그아웃 (Refresh Token 무효화)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // Authorization 헤더에서 Bearer 토큰 추출
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // 토큰에서 이메일 추출
            String email;
            try {
                Claims claims = Jwts.parserBuilder()
                                    .setSigningKey(jwtSecret.getBytes())
                                    .build()
                                    .parseClaimsJws(token).getBody();
                email = claims.getSubject();
            } catch (ExpiredJwtException ex) {
                // 만료된 토큰의 경우 예외에서 claims 추출
                email = ex.getClaims().getSubject();
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            // 해당 사용자의 Refresh Token 제거 (로그아웃 처리)
            accountRepository.findByEmail(email).ifPresent(acc -> {
                acc.setRefreshToken(null);
                accountRepository.save(acc);
            });
        }
        return ResponseEntity.ok().build();
    }

    // Access Token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String token = authHeader.substring(7);
        String email;
        try {
            // 만료 여부와 관계없이 일단 파싱 시도
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(jwtSecret.getBytes())
                                .build()
                                .parseClaimsJws(token).getBody();
            email = claims.getSubject();
        } catch (ExpiredJwtException ex) {
            // 토큰 만료 시 ExpiredJwtException에서 claims 꺼내기
            email = ex.getClaims().getSubject();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        // 계정 및 Refresh Token 확인
        Account account = accountRepository.findByEmail(email).orElse(null);
        if (account == null || account.getRefreshToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        // 서버에 저장된 Refresh Token 검증 (유효기간 확인)
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build().parseClaimsJws(account.getRefreshToken());
        } catch (Exception ex) {
            // 저장된 Refresh Token이 만료됨 -> 새 로그인 필요
            account.setRefreshToken(null);
            accountRepository.save(account);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        // Refresh Token이 유효하면 새로운 Access Token 생성
        String newAccessToken = Jwts.builder()
                .setSubject(account.getEmail())
                .claim("userId", account.getUserId())
                .claim("role", account.getRole())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        // 응답 객체 생성 (새 Access Token + 사용자 정보)
        AuthResponse response = new AuthResponse(newAccessToken, account.getEmail(), account.getUsername());
        return ResponseEntity.ok(response);
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
