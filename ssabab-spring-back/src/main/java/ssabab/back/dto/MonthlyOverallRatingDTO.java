// dto.MonthlyOverallRatingDTO
package ssabab.back.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class MonthlyOverallRatingDTO {
    private Double average;
    private Long totalEvaluations;
}
