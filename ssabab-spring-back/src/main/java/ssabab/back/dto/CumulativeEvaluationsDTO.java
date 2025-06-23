// dto.CumulativeEvaluationsDTO
package ssabab.back.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CumulativeEvaluationsDTO {
    private Integer currentMonth;
    private Integer totalCumulative;
    private Integer previousMonthCumulative;
}
