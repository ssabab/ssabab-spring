package ssabab.back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "SSABAB BACK API";   // 간단한 문자열 JSON 아님
    }
}
