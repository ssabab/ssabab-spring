// dto.RatingDistributionDTO
package ssabab.back.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RatingDistributionDTO {
    private Double min;
    private Double max;
    private Double avg;
    private Double iqrStart;
    private Double iqrEnd;
    private Double variance;
    private Double stdDev;
}
