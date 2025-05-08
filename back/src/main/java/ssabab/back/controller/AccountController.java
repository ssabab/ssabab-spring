package ssabab.back.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.AccountDTO;
import ssabab.back.service.AccountService;

import java.util.List;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /* ---------- 화면용 뷰 ---------- */
    @GetMapping("/save")   public String saveForm()  { return "save";  }
    @GetMapping("/login")  public String loginForm() { return "login"; }
    @GetMapping("/")       public String listPage()  { return "list";  }

    /* ---------- 회원가입 처리 ---------- */
    @PostMapping("/save")
    public String save(@ModelAttribute AccountDTO dto) {
        accountService.save(dto);
        return "login";   // 가입 후 로그인 페이지로 이동
    }

    /* ---------- 로그인 처리 ---------- */
    @PostMapping("/login")
    public String login(@ModelAttribute AccountDTO dto, HttpSession session) {
        AccountDTO user = accountService.login(dto);
        if (user != null) {
            session.setAttribute("loginEmail", user.getEmail());
            return "main";
        }
        return "login";   // 실패 시 다시 로그인 화면
    }

    /* ---------- 로그아웃 ---------- */
    @GetMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("logout");
    }

    /* ---------- 회원 목록 & 상세 ---------- */
    @GetMapping("/list-data")
    @ResponseBody
    public List<AccountDTO> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public ResponseEntity<?> findById(@PathVariable Integer userId) {
        AccountDTO dto = accountService.findByuserId(userId);
        return dto != null ? ResponseEntity.ok(dto)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
    }

    /* ---------- 이메일 중복 Ajax ---------- */
    @PostMapping("/email-check")
    @ResponseBody
    public String emailCheck(@RequestParam String accountEmail) {
        return accountService.emailExists(accountEmail) ? "duplicate" : "ok";
    }
}
