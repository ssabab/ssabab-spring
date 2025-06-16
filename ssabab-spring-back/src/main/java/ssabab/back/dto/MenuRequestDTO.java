package ssabab.back.dto;

import lombok.Getter;
import lombok.Setter;
import ssabab.back.enums.FoodCategory;
import ssabab.back.enums.FoodMainSub;
import ssabab.back.enums.FoodTag;

import java.time.LocalDate;
import java.util.List;

/**
 * 메뉴 수정 요청 DTO (PUT /api/menu/{menuId} 전용)
 */
@Getter
@Setter
public class MenuRequestDTO {
    private LocalDate date;
    // private int menuOrder; // menuOrder 필드 없음
    private List<FoodRequestDTO> foods;

    @Getter @Setter
    public static class FoodRequestDTO {
        private String foodName;
        private FoodMainSub mainSub;
        private FoodCategory category;
        private FoodTag tag;
    }
}