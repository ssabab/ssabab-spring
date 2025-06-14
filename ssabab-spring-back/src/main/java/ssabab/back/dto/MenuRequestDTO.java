package ssabab.back.dto;

import lombok.Getter;
import ssabab.back.enums.FoodCategory;
import ssabab.back.enums.FoodMainSub;
import ssabab.back.enums.FoodTag;

import java.time.LocalDate;
import java.util.List;

/**
 * 메뉴 등록 및 수정 요청 DTO
 */
@Getter
public class MenuRequestDTO {
    private LocalDate date;
    private List<FoodRequestDTO> foods;

    @Getter
    public static class FoodRequestDTO {
        private String foodName;
        private FoodMainSub mainSub;
        private FoodCategory category;
        private FoodTag tag;
    }
}
