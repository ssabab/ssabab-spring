package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalAnalysisResponse {
    // 테이블명: dm_user_summary
    private RatingDataDTO dm_user_summary;
    // 테이블명: dm_user_food_rating_rank (best, worst로 분리)
    private List<FoodRatingRankDTO> dm_user_food_rating_rank_best;
    private List<FoodRatingRankDTO> dm_user_food_rating_rank_worst;
    // 테이블명: dm_user_category_stats
    private List<CategoryStatsDTO> dm_user_category_stats;
    // 테이블명:  dm_user_tag_stats
    private List<TagStatsDTO> dm_user_tag_stats;
    // 테이블명: dm_user_review_word
    private List<ReviewWordDTO> dm_user_review_word;

    // 테이블명: dm_user_insight
    private UserInsightDTO dm_user_insight;

    // 테이블명: dm_user_group_comparison
    private UserGroupComparisonDTO dm_user_group_comparison;
}