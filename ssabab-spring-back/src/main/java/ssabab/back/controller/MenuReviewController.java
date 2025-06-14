package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.MenuReviewRequestDTO;
import ssabab.back.dto.FriendMenuReviewResponseDTO;
import ssabab.back.service.MenuReviewService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 메뉴 리뷰 관련 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review/menu")
public class MenuReviewController {

    private final MenuReviewService menuReviewService;

    /**
     * 메뉴 후회 여부 및 한 줄 평 등록 또는 수정
     * 로그인된 사용자 기준
     */
    @PostMapping
    public ResponseEntity<Object> submitMenuReview(@RequestBody MenuReviewRequestDTO request) {
        try {
            menuReviewService.submitMenuReview(request);
            return ResponseEntity.ok(Map.of("message", "메뉴 리뷰 저장 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 친구들이 남긴 메뉴 리뷰(평균 평점) 조회
     * 로그인된 사용자 기준
     */
    @GetMapping("/friends")
    public ResponseEntity<Object> getFriendsMenuReviewStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<FriendMenuReviewResponseDTO> stats = menuReviewService.getFriendsMenuReviewStats(date);
            return ResponseEntity.ok(Map.of("reviews", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}