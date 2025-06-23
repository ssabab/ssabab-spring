// controller.MonthlyAnalysisController.java
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssabab.back.dto.MonthlyAnalysisResponse;
import ssabab.back.service.MonthlyAnalysisService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis/monthly")
public class MonthlyAnalysisController {

    private final MonthlyAnalysisService monthlyAnalysisService;

    @GetMapping
    public ResponseEntity<Object> getMonthlyAnalysis() {
        try {
            MonthlyAnalysisResponse response = monthlyAnalysisService.getMonthlyAnalysisData();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "월간 분석 데이터 조회 실패: " + e.getMessage()));
        }
    }
}