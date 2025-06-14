// ssabab/back/dto/FoodPreferenceDto.java
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodPreferenceDTO {
    private String name; // 카테고리명 또는 태그명
    private Integer count; // 선호도 카운트
}