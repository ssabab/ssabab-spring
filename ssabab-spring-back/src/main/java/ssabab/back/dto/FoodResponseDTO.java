package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import ssabab.back.entity.Food;
import ssabab.back.enums.FoodMainSub;
import ssabab.back.enums.FoodCategory;
import ssabab.back.enums.FoodTag;

/**
 * 특정 날짜의 메뉴에 속한 음식 정보 DTO (응답용)
 */
@Getter
@Builder
public class FoodResponseDTO {
    private final Long foodId; // foodId 유지
    private final String foodName;
    private final FoodMainSub mainSub;
    private final FoodCategory category;
    private final FoodTag tag;

    public static FoodResponseDTO from(Food food) {
        return FoodResponseDTO.builder()
                .foodId(food.getFoodId())
                .foodName(food.getFoodName())
                .mainSub(food.getMainSub())
                .category(food.getCategory())
                .tag(food.getTag())
                .build();
    }
}