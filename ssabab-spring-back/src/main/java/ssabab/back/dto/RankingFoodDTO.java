// dto.RankingFoodDTO
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingFoodDTO {
    private String foodName;
    private Float score; // ERD에 FLOAT으로 되어있으므로 Float 사용
    // 랭킹 정보도 필요하다면 추가
}