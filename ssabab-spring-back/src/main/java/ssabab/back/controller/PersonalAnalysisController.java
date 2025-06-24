// controller/PersonalAnalysisController.java
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssabab.back.dto.PersonalAnalysisResponse;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;
import ssabab.back.service.PersonalAnalysisService;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/analysis/personal")
@RequiredArgsConstructor
public class PersonalAnalysisController {

    private final PersonalAnalysisService personalAnalysisService;
    private final AccountRepository accountRepository; // AccountRepository 주입

    @GetMapping
    public ResponseEntity<Object> getPersonalAnalysis(Authentication authentication) {
        // 1. 인증 정보 확인
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized", "message", "개인 분석은 로그인 후 이용 가능합니다."));
        }

        // 2. 인증 정보로부터 사용자 ID 추출 (올바른 방식)
        Long userId;
        try {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            Account user = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("인증된 사용자 정보를 DB에서 찾을 수 없습니다."));
            userId = user.getUserId();
        } catch (ClassCastException | NoSuchElementException e) {
            return ResponseEntity.status(401).body(Map.of("error", "InvalidAuthentication", "message", "인증 정보가 올바르지 않습니다."));
        }

        // 3. 서비스 호출 및 예외 처리
        try {
            PersonalAnalysisResponse response = personalAnalysisService.getPersonalAnalysis(userId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            // 서비스에서 데이터를 찾지 못했을 때 발생하는 예외
            return ResponseEntity.status(404).body(Map.of("error", "DataNotFound", "message", e.getMessage()));
        } catch (Exception e) {
            // 그 외 예상치 못한 모든 서버 내부 예외 처리
            return ResponseEntity.status(500).body(Map.of("error", "InternalServerError", "message", "개인 분석 데이터 조회 중 오류가 발생했습니다."));
        }
    }
}