package ssabab.back.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.AccountDTO;
import ssabab.back.dto.LoginDTO;
import ssabab.back.service.AccountService;

import java.util.List;
import java.util.Map;

/**
 * JSON-전용 계정 API
 *  └ POST /account/signup   – 회원가입
 *  └ POST /account/login    – 로그인(성공 시 {"userId": 1})
 *  └ POST /account/logout   – 로그아웃
 *  └ GET  /account          – 회원 전체 목록
 *  └ GET  /account/{id}     – 단일 회원 조회
 *  └ POST /account/email-check – 이메일 중복 검사
 */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /* ---------- 회원가입 ---------- */
    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> signup(@RequestBody AccountDTO dto) {
        accountService.save(dto);
        return ResponseEntity.ok(Map.of("message", "SIGNUP_OK"));
    }

    /* ---------- 로그인 ---------- */
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto, HttpSession session) {
        AccountDTO user = accountService.login(dto);
        if (user != null) {
            session.setAttribute("loginEmail", user.getEmail());       // 세션 유지
            return ResponseEntity.ok(Map.of("userId", user.getId())); // userId 키로 반환
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "LOGIN_FAIL"));
    }

    /* ---------- 로그아웃 ---------- */
    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "LOGOUT_OK"));
    }

    /* ---------- 회원 전체 목록 ---------- */
    @GetMapping(produces = "application/json")          // GET /account
    public List<AccountDTO> findAll() {
        return accountService.findAll();
    }

    /* ---------- 단일 회원 조회 ---------- */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        AccountDTO dto = accountService.findById(id);
        return dto != null ? ResponseEntity.ok(dto)
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "NOT_FOUND"));
    }

    /* ---------- 이메일 중복 검사 ---------- */
    @PostMapping(value = "/email-check", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> emailCheck(@RequestBody String accountEmail) {
        boolean duplicate = accountService.emailExists(accountEmail.replace("\"", ""));
        return ResponseEntity.ok(Map.of("duplicate", duplicate));
    }
}
