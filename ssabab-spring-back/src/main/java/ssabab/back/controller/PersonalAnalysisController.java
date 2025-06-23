// controller.PersonalAnalysisController.java
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssabab.back.dto.PersonalAnalysisResponse;
import ssabab.back.service.PersonalAnalysisService;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/analysis/personal")
@RequiredArgsConstructor
public class PersonalAnalysisController {

    private final PersonalAnalysisService personalAnalysisService;

    @GetMapping
    public ResponseEntity<Object> getPersonalAnalysis(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", "개인 분석은 로그인 후 이용 가능합니다."));
        }

        try {
            PersonalAnalysisResponse response = personalAnalysisService.getPersonalAnalysisData();
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage(), "message", "사용자 정보를 찾을 수 없거나 분석 데이터가 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "개인 분석 데이터 조회 실패: " + e.getMessage()));
        }
    }
}