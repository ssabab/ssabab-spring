// dto.PersonalAnalysisResponse
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PersonalAnalysisResponse {
    private RatingDataDTO ratingData;
    private List<TopLowestRatedFoodsDTO> topRatedFoods;
    private List<TopLowestRatedFoodsDTO> lowestRatedFoods;
    private List<PreferredCategoryDTO> preferredCategories;
    private List<PreferredKeywordDTO> preferredKeywordsForCloud;
    private String personalInsight;
    private ComparisonDataDTO comparisonData;
}