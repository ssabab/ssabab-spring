// dto/analysis/CumulativeEvaluationsDTO.java
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CumulativeEvaluationsDTO {
    private Long currentMonth; // Integer -> Long
    private Long totalCumulative; // Integer -> Long
    private Long previousMonthCumulative; // Integer -> Long
}