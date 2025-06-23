// dto.MonthlyAnalysisResponse
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MonthlyAnalysisResponse {
    private List<TopFoodDTO> topFoods;
    private List<TopFoodDTO> worstFoods;
    private MonthlyVisitorsDTO monthlyVisitors;
    private CumulativeEvaluationsDTO cumulativeEvaluations;
    private RatingDistributionDTO ratingDistribution;
    private List<FrequentVisitorDTO> frequentVisitors;
    private MonthlyOverallRatingDTO monthlyOverallRating;
}