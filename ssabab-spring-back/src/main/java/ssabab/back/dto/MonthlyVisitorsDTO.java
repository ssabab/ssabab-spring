// dto.MonthlyVisitorsDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyVisitorsDTO {
    private Long current;
    private Long previous;
    private Long totalCumulative;
    private Long previousMonthCumulative;
}
