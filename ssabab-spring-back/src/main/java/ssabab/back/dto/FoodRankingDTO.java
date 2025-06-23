// dto.FoodRankingDTO
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodRankingDTO {
    private String foodName;
    private Double avgScore;
    private int rank;
}