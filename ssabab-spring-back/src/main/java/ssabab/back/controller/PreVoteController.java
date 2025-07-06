// controller/PreVoteController.java
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.FriendPreVoteResponseDTO;
import ssabab.back.dto.PreVoteRequestDTO;
import ssabab.back.dto.PreVoteResponseDTO;
import ssabab.back.service.PreVoteService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 사전 투표 관련 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vote")
public class PreVoteController {

    private final PreVoteService preVoteService;


    /**
     * 현재 사용자의 특정 날짜 사전 투표 정보 조회
     * @param date 조회할 날짜 (yyyy-MM-dd 형식)
     * @return 투표한 menuId가 담긴 응답. 투표 내역이 없으면 menuId는 null.
     */
    @GetMapping
    public ResponseEntity<Object> getUserPreVote(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            PreVoteResponseDTO result = preVoteService.getUserPreVoteForDate(date);
            // 프론트에서는 응답받은 객체의 menuId가 null인지 아닌지로 투표 여부를 판단할 수 있습니다.
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 로그인 안됨 등의 예외 처리
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 사전 투표 등록 또는 수정
     * 로그인된 사용자 기준
     */
    @PostMapping
    public ResponseEntity<Object> submitPreVote(@RequestBody PreVoteRequestDTO request) {
        try {
            preVoteService.submitPreVote(request);
            return ResponseEntity.ok(Map.of("message", "사전 투표 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 친구들의 사전 투표 결과 조회
     * 로그인된 사용자 기준
     */
    @GetMapping("/friends")
    public ResponseEntity<Object> getFriendsPreVotes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<FriendPreVoteResponseDTO> results = preVoteService.getFriendsPreVotes(date);
            return ResponseEntity.ok(Map.of("votes", results));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}