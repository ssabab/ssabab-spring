// dto.FrequentVisitorsDTO
package ssabab.back.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class FrequentVisitorDTO {
    private String name;
    private Long visits;
    private String lastVisit;
}
