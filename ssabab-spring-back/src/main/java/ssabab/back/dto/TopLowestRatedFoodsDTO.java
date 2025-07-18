// dto.TopLowestRatedFoodsDTO
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopLowestRatedFoodsDTO{
    private String name;
    private Double rating;
    private String date;
}