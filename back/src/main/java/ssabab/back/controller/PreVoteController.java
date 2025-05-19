package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.service.PreVoteService;

import java.util.Map;

/**
 * 사전 투표 API
 * PUT /prevote/{menuId} - 특정 메뉴에 대한 사전 투표 등록 또는 수정
 * GET /prevote/{menuId}/count - 특정 메뉴의 투표 수 조회
 * GET /prevote/{menuId}/status - 사용자의 특정 메뉴 투표 여부 확인
 */
@RestController
@RequestMapping("/prevote")
@RequiredArgsConstructor
public class PreVoteController {

    private final PreVoteService preVoteService;

    /**
     * 특정 메뉴에 대한 사전 투표를 등록하거나 수정합니다.
     * 사용자가 이미 투표한 경우 기존 투표를 수정하고, 투표하지 않은 경우 새로운 투표를 생성합니다.
     * 
     * @param menuId 메뉴 ID
     * @param userId 사용자 ID (헤더에서 추출)
     * @return 응답 데이터
     */
    @PutMapping("/{menuId}")
    public ResponseEntity<?> createOrUpdatePreVote(
            @PathVariable Integer menuId,
            @RequestHeader("userId") Integer userId) {
        
        boolean success = preVoteService.createOrUpdatePreVote(menuId, userId);
        
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "사전 투표가 성공적으로 처리되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "사전 투표 처리 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특정 메뉴의 투표 수를 조회합니다.
     * 
     * @param menuId 메뉴 ID
     * @return 투표 수
     */
    @GetMapping("/{menuId}/count")
    public ResponseEntity<?> getVoteCount(@PathVariable Integer menuId) {
        try {
            int count = preVoteService.getVoteCountByMenuId(menuId);
            return ResponseEntity.ok(Map.of("menuId", menuId, "voteCount", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 사용자가 특정 메뉴에 투표했는지 확인합니다.
     * 
     * @param menuId 메뉴 ID
     * @param userId 사용자 ID (헤더에서 추출)
     * @return 투표 여부
     */
    @GetMapping("/{menuId}/status")
    public ResponseEntity<?> checkVoteStatus(
            @PathVariable Integer menuId,
            @RequestHeader("userId") Integer userId) {
        
        boolean hasVoted = preVoteService.hasVoted(menuId, userId);
        return ResponseEntity.ok(Map.of("menuId", menuId, "userId", userId, "hasVoted", hasVoted));
    }
} 