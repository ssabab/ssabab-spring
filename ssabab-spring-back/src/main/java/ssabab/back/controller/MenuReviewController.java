// controller.MenuReviewController
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus; // HttpStatus 임포트 (CREATED 상태 코드 사용 위함)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.MenuReviewRequestDTO;
import ssabab.back.dto.FriendMenuReviewResponseDTO;
import ssabab.back.dto.MenuReviewResponseDTO;
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
     * 메뉴 리뷰 등록 (새로운 리뷰 생성)
     * POST /api/review/menu
     * 로그인된 사용자 기준
     */
    @PostMapping
    public ResponseEntity<Object> createMenuReview(@RequestBody MenuReviewRequestDTO request) { // 변경: 메서드 이름
        try {
            menuReviewService.submitMenuReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "메뉴 리뷰 등록 완료")); // 201 Created 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 현재 사용자의 특정 날짜 메뉴 리뷰 정보 조회
     * @param date 조회할 날짜 (yyyy-MM-dd 형식)
     * @return 리뷰를 작성한 메뉴 ID가 담긴 DTO 목록. 리뷰가 없다면 빈 리스트 `[]`를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<Object> getUserMenuReview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            MenuReviewResponseDTO result = menuReviewService.getUserReviewedMenuForDate(date);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 로그인 안됨 등의 예외 처리
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    /**
     * 메뉴 리뷰 수정 (기존 리뷰 업데이트)
     * PUT /api/review/menu/{menuId}
     * 로그인된 사용자 기준
     */
    @PutMapping("/{menuId}") //
    public ResponseEntity<Object> updateMenuReview( // 변경: 메서드 이름
                                                    @PathVariable Long menuId, // 변경: menuId 경로 변수
                                                    @RequestBody MenuReviewRequestDTO request) {
        try {
            if (!menuId.equals(request.getMenuId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "경로의 menuId와 요청 본문의 menuId가 일치하지 않습니다."));
            }
            menuReviewService.submitMenuReview(request); // 기존 서비스 메서드 재사용
            return ResponseEntity.ok(Map.of("message", "메뉴 리뷰 수정 완료")); // 200 OK 반환
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