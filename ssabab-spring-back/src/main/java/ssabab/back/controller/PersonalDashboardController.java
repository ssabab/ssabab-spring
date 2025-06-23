// controller.PersonalDashboardController
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.PersonalDashboardResponse;
import ssabab.back.service.PersonalDashboardService;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 개인 분석 대시보드 API 컨트롤러
 * SRS: 개인 분석(잠금) (클릭 시 로그인 요청 및 로그인 페이지 팝업)
 */
@RestController
@RequestMapping("/api/dashboard/personal")
@RequiredArgsConstructor
public class PersonalDashboardController {

    private final PersonalDashboardService personalDashboardService;

    @GetMapping
    public ResponseEntity<Object> getPersonalDashboard(Authentication authentication) {
        // SRS에 따라 개인 분석은 로그인된 사용자만 접근 가능 (잠금)
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", "개인 분석은 로그인 후 이용 가능합니다."));
        }

        try {
            PersonalDashboardResponse response = personalDashboardService.getDashboard(); // 서비스에서 로그인된 사용자 정보를 가져옴
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage(), "message", "사용자 정보를 찾을 수 없거나 대시보드 데이터가 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "개인 대시보드 조회 실패: " + e.getMessage()));
        }
    }
}