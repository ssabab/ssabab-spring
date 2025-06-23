// dto.MonthlyOverallRatingDTO
package ssabab.back.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MonthlyOverallRatingDTO {
    private Double average;
    private Integer totalEvaluations;
}
