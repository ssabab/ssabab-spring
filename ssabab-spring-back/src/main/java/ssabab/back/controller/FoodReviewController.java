package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
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
     * 음식 평점 등록 또는 수정 + 메뉴 평균 평점 반영
     * 로그인된 사용자 기준
     */
    @PostMapping
    public ResponseEntity<Object> submitFoodReviews(
            @RequestBody FoodReviewRequestDTO request) {
        try {
            foodReviewService.submitFoodReviews(request.getMenuId(), request.getReviews());
            return ResponseEntity.ok(Map.of("message", "음식 평점 저장 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}