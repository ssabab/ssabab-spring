// ssabab/back/dto/PersonalDashboardResponse.java
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDashboardResponse {
    private List<FoodRankingDTO> bestFoods; // 개인 선호 Best 음식 Top 5
    private List<FoodRankingDTO> worstFoods; // 개인 선호 Worst 음식 Top 5
    private List<FoodPreferenceDTO> topCategoryPreference; // 최빈 카테고리
    private List<FoodPreferenceDTO> topTagPreference; // 최빈 태그
    private String evaluationTendency; // 전체 평점 대비 본인 평점 비교 (엄격, 비슷, 후한)
}