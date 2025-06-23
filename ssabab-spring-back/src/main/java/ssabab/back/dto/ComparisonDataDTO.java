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
class ComparisonDataDTO{
    private Double myRating;
    private Double avgRatingCommunity;
    private Double mySpicyPreference;
    private Double avgSpicyPreference;
    private Double myVarietySeeking;
    private Double avgVarietyCommunity;
}