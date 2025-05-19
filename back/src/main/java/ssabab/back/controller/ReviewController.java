package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.FoodReviewRequestDTO;
import ssabab.back.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{menuId}")
    public ResponseEntity<FoodReviewRequestDTO> getReviews(
            @RequestHeader("userId") Integer userId,
            @PathVariable Integer menuId) {
        return ResponseEntity.ok(reviewService.getFoodReviews(userId, menuId));
    }

    @PostMapping("/{menuId}")
    public ResponseEntity<Void> createReviews(
            @RequestHeader("userId") Integer userId,
            @PathVariable Integer menuId,
            @RequestBody FoodReviewRequestDTO dto) {
        reviewService.createOrUpdateFoodReviews(userId, menuId, dto, false);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<Void> updateReviews(
            @RequestHeader("userId") Integer userId,
            @PathVariable Integer menuId,
            @RequestBody FoodReviewRequestDTO dto) {
        reviewService.createOrUpdateFoodReviews(userId, menuId, dto, true);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteReviews(
            @RequestHeader("userId") Integer userId,
            @PathVariable Integer menuId) {
        reviewService.deleteFoodReviews(userId, menuId);
        return ResponseEntity.ok().build();
    }
}
