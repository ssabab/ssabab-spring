package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import ssabab.back.entity.FoodReview;
import java.util.List;

/**
 * 음식 리뷰 등록 요청 DTO - 메뉴 ID + 음식 리뷰 목록
 */
@Getter
@Builder
public class FoodReviewRequestDTO {
    private final Long menuId;
    private final List<ReviewItem> reviews;

    /**
     * 개별 음식 리뷰 DTO
     */
    @Getter
    @Builder
    public static class ReviewItem {
        private final Long foodId;
        private final Long foodScore;

        public static ReviewItem from(FoodReview foodReview) {
            return ReviewItem.builder()
                    .foodId(foodReview.getFood().getFoodId())
                    .foodScore(foodReview.getFoodScore())
                    .build();
        }
    }
}
