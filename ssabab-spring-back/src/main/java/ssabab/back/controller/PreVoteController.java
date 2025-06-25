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
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vote")
public class PreVoteController {

    private final PreVoteService preVoteService;

    // 사전 투표 등록
    @PostMapping
    public ResponseEntity<Object> registerPreVote(@RequestBody PreVoteRequestDTO request) {
        try {
            preVoteService.registerPreVote(request);
            return ResponseEntity.ok(Map.of("message", "사전 투표가 등록되었습니다"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage())); // 중복 투표
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 사전 투표 수정
    @PutMapping
    public ResponseEntity<Object> updatePreVote(@RequestBody PreVoteRequestDTO request) {
        try {
            preVoteService.updatePreVote(request);
            return ResponseEntity.ok(Map.of("message", "사전 투표가 수정되었습니다"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); // 기존 투표 없음
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 친구들의 사전 투표 결과 조회
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
