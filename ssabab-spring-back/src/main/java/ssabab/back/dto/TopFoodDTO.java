// dto.TopFoodDTO
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopFoodDTO {
    private String name;
    private Long reviews;
    private Double rating;
}