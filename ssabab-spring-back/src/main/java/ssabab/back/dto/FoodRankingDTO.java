// ssabab/back/dto/FoodRankingDto.java (Monthly Dashboard용 새로 추가)
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