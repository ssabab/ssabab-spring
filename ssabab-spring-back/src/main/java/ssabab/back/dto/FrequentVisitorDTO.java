// dto.FrequentVisitorsDTO
package ssabab.back.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class FrequentVisitorDTO {
    private String name;
    private Integer visits;
    private String lastVisit;
}
