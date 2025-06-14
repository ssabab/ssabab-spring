package ssabab.back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * 메인 홈 화면 관련 컨트롤러 (로그인 후 리디렉션 경로)
 */
@RestController
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<Object> home(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = "Guest"; // 기본값
        Object principal = authentication.getPrincipal();

        // JWT 인증 후에는 UserDetails 타입으로 principal이 설정됩니다.
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername(); // 보통 email이 username으로 사용됨
        } else if (principal instanceof String) {
            username = (String) principal; // 만약 principal이 String 형태의 username/email인 경우
        }

        Map<String, String> resp = new HashMap<>();
        resp.put("message", "Welcome " + username + "!");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/analysis")
    public ResponseEntity<Object> analysisPage(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // 로그인 X 케이스: 일부 정보만 보여주거나 로그인 유도 메시지 반환
            return ResponseEntity.ok(Map.of("message", "분석 페이지입니다. 로그인하면 더 많은 정보를 볼 수 있습니다."));
        }
        // 로그인 O 케이스: 전체 분석 정보 제공
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return ResponseEntity.ok(Map.of("message", "Welcome to the Analysis Page, " + username + "!", "accessLevel", "full"));
    }
}