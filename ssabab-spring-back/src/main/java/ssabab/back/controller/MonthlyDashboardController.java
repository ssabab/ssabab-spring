// ssabab/back/controller/MonthlyDashboardController.java
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.MonthlyDashboardResponse;
import ssabab.back.service.MonthlyDashboardService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard/monthly")
public class MonthlyDashboardController {

    private final MonthlyDashboardService monthlyDashboardService;

    @GetMapping
    public ResponseEntity<Object> getMonthlyDashboard(Authentication authentication) {
        // SRS: <로그인 X 케이스> 분석 페이지 -> 월간 분석 (로그인 없이 접근 가능)
        // authentication 객체는 JWT 필터에 의해 자동으로 채워지므로, 로그인 여부 확인 가능.
        // 비로그인 사용자도 접근 가능하므로, 인증 여부와 관계없이 데이터를 반환합니다.
        try {
            MonthlyDashboardResponse response = monthlyDashboardService.getDashboard();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "월간 대시보드 조회 실패: " + e.getMessage()));
        }
    }
}