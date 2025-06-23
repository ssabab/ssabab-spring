// dto.ComparisonDataDTO
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonDataDTO{
    private Double myRating;
    private Double avgRatingCommunity;
    private Double mySpicyPreference;
    private Double avgSpicyCommunity;
    private Double myVarietySeeking;
    private Double avgVarietyCommunity;
}