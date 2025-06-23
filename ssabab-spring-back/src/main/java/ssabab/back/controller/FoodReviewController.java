// controller.FoodReviewController
package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // HttpStatus 임포트 (CREATED 상태 코드 사용 위함)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.FoodReviewRequestDTO;
import ssabab.back.service.FoodReviewService;

import java.util.Map;

/**
 * 음식 리뷰 관련 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review/food")
public class FoodReviewController {

    private final FoodReviewService foodReviewService;

    /**
     * 음식 평점 등록 (새로운 리뷰 생성)
     * POST /api/review/food
     * 로그인된 사용자 기준
     */
    @PostMapping
    public ResponseEntity<Object> createFoodReviews(@RequestBody FoodReviewRequestDTO request) {
        try {
            foodReviewService.submitFoodReviews(request.getMenuId(), request.getReviews()); // 기존 서비스 메서드 재사용
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "음식 평점 등록 완료")); // 201 Created 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 음식 평점 수정 (기존 리뷰 업데이트)
     * PUT /api/review/food/{menuId}
     * 로그인된 사용자 기준
     */
    @PutMapping("/{menuId}") //
    public ResponseEntity<Object> updateFoodReviews(@PathVariable Long menuId, @RequestBody FoodReviewRequestDTO request) {
        try {
            if (!menuId.equals(request.getMenuId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "경로의 menuId와 요청 본문의 menuId가 일치하지 않습니다."));
            }
            foodReviewService.submitFoodReviews(request.getMenuId(), request.getReviews()); // 기존 서비스 메서드 재사용
            return ResponseEntity.ok(Map.of("message", "음식 평점 수정 완료")); // 200 OK 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}