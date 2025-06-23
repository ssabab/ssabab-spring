// controller.PreVoteController
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.PreVoteRequestDTO;
import ssabab.back.dto.FriendPreVoteResponseDTO;
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