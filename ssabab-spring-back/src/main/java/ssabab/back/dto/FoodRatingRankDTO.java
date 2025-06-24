// dto.RankingFoodDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ssabab.back.enums.ScoreType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodRatingRankDTO {
    // dm_user_food_rating_rank
    private Long userId;
    private String foodName;
    private Float foodScore;
    private Integer rankOrder;
    private ScoreType scoreType; // "best" 또는 "worst"
}