// controller.HomeController
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ssabab.back.entity.Account;
import ssabab.back.service.AccountService;

import java.util.HashMap;
import java.util.Map;

/**
 * 메인 홈 화면 관련 컨트롤러 (로그인 후 리디렉션 경로)
 */
@RequiredArgsConstructor
@RestController
public class HomeController {
    @Autowired
    private final AccountService accountService;

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

    /**
     * 관리자 페이지 엔드포인트
     * 오직 'ADMIN' 역할을 가진 사용자만 접근 가능합니다.
     */
    @GetMapping("/admin")
    public ResponseEntity<Object> adminAccessCheck(Authentication authentication) { // 변경: @PreAuthorize 제거
        // 1. 로그인 여부 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            // 로그인되어 있지 않으면 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized", "message", "로그인이 필요합니다."));
        }

        // 2. 현재 로그인된 사용자 정보 가져오기 (이메일로)
        String userEmail = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            userEmail = userDetails.getUsername(); // UserDetails의 username은 이메일
        } else {
            // UserDetails 타입이 아니면 403 Forbidden 응답
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Forbidden", "message", "인증 정보에서 사용자 이메일을 확인할 수 없습니다."));
        }

        // 3. 데이터베이스에서 사용자 정보 및 역할 직접 조회
        // AccountService를 통해 DB에서 사용자의 Account 엔티티를 가져옵니다.
        Account user = null;
        try {
            user = accountService.getLoginUser(); // AccountService의 getLoginUser 재사용 또는 직접 로직 추가
        } catch (Exception e) { // 사용자 정보를 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Forbidden", "message", "사용자 정보를 찾을 수 없거나 권한 확인에 실패했습니다."));
        }

        // 4. 역할이 'ADMIN'인지 확인 (DB에서 가져온 role 값 사용)
        if (user != null && "ADMIN".equals(user.getRole())) {
            // 역할이 ADMIN이면 200 OK 응답
            Map<String, String> resp = new HashMap<>();
            resp.put("message", "관리자 페이지에 오신 것을 환영합니다, " + user.getUsername() + "님!");
            resp.put("accessLevel", "admin");
            return ResponseEntity.ok(resp);
        } else {
            // 역할이 ADMIN이 아니면 403 Forbidden 응답
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Forbidden", "message", "이 페이지에 접근할 권한이 없습니다."));
        }
    }
}