// dto.MonthlyVisitorsDTO
package ssabab.back.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MonthlyVisitorsDTO {
    private Integer current;
    private Integer previous;
    private Integer totalCumulative;
    private Integer previousMonthCumulative;
}
