package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.analysis.DailyAnalysisDTO;
import ssabab.back.dto.analysis.MonthlyAnalysisDTO;
import ssabab.back.dto.analysis.PersonalAnalysisDTO;
import ssabab.back.service.AnalysisService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {
    
    private final AnalysisService analysisService;
    
    /**
     * 개인 분석 데이터를 조회합니다.
     * @param userId 사용자 ID
     * @return 개인 분석 데이터
     */
    @GetMapping("/personal/{userId}")
    public ResponseEntity<PersonalAnalysisDTO> getPersonalAnalysis(@PathVariable Integer userId) {
        return ResponseEntity.ok(analysisService.getPersonalAnalysis(userId));
    }
    
    /**
     * 월별 분석 데이터를 조회합니다.
     * @param yearMonth 년월 (예: 202404)
     * @return 월별 분석 데이터
     */
    @GetMapping("/monthly/{yearMonth}")
    public ResponseEntity<MonthlyAnalysisDTO> getMonthlyAnalysis(@PathVariable String yearMonth) {
        return ResponseEntity.ok(analysisService.getMonthlyAnalysis(yearMonth));
    }
    
    /**
     * 일별 분석 데이터를 조회합니다.
     * @param date 날짜 (예: 2024-04-20)
     * @return 일별 분석 데이터
     */
    @GetMapping("/daily/{date}")
    public ResponseEntity<DailyAnalysisDTO> getDailyAnalysis(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(analysisService.getDailyAnalysis(date));
    }
} 