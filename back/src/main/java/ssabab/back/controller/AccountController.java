package ssabab.back.controller;

import ssabab.back.dto.AccountDTO;
import ssabab.back.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AccountController {
    // 생성자 주입
    private final AccountService accountService;

    // 회원가입 페이지 출력 요청
    @GetMapping("/back/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/back/save")
    public String save(@ModelAttribute AccountDTO accountDTO) {
        System.out.println("AccountController.save");
        System.out.println("accountDTO = " + accountDTO);
        accountService.save(accountDTO);
        return "login";
    }

    @GetMapping("/back/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/back/login")
    public String login(@ModelAttribute AccountDTO accountDTO, HttpSession session) {
        AccountDTO loginResult = accountService.login(accountDTO);
        if (loginResult != null) {
            // login 성공
            session.setAttribute("loginEmail", loginResult.getAccountEmail());
            return "main";
        } else {
            // login 실패
            return "login";
        }
    }

    @GetMapping("/back/")
    public String findAll(Model model) {
        List<AccountDTO> accountDTOList = accountService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model사용
        model.addAttribute("accountList", accountDTOList);
        return "list";
    }

    @GetMapping("/Account/{userId}")
    public String findByuserId(@PathVariable Long userId, Model model) {
        AccountDTO accountDTO = accountService.findByuserId(userId);
        model.addAttribute("account", accountDTO);
        return "detail";
    }

    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model) {
        String myEmail = (String) session.getAttribute("loginEmail");
        AccountDTO accountDTO = accountService.updateForm(myEmail);
        model.addAttribute("updateAccount", accountDTO);
        return "update";
    }

    @PostMapping("/member/update")
    public String update(@ModelAttribute AccountDTO accountDTO) {
        accountService.update(accountDTO);
        return "redirect:/Account/" + accountDTO.getuserId();
    }

    

    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    @PostMapping("/member/email-check")
    public @ResponseBody String emailCheck(@RequestParam("accountEmail") String accountEmail) {
        System.out.println("accountEmail = " + accountEmail);
        String checkResult = accountService.emailCheck(accountEmail);
        return checkResult;

    }

}









