package ssabab.back.dto;

import lombok.*;
import ssabab.back.entity.FoodCategory;
import ssabab.back.entity.SpicyLevel;
import ssabab.back.entity.Taste;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodDTO {
    private Integer foodId;
    private String foodName;
    private String mainSub; // 주메뉴 또는 서브메뉴 문자열로 처리
    private FoodCategory category;
    private String tag; // 태그 문자열로 처리
    private Integer foodScore;
    private Taste taste;
    private SpicyLevel spicyLevel;
    
    // category setter를 오버라이드하여 문자열을 FoodCategory로 변환
    public void setCategory(String categoryStr) {
        if (categoryStr == null) return;
        
        switch (categoryStr.toLowerCase()) {
            case "한식":
                this.category = FoodCategory.KOREAN;
                break;
            case "중식":
                this.category = FoodCategory.CHINESE;
                break;
            case "일식":
                this.category = FoodCategory.JAPANESE;
                break;
            case "양식":
                this.category = FoodCategory.WESTERN;
                break;
            case "퓨전":
                this.category = FoodCategory.FUSION;
                break;
            case "분식":
                this.category = FoodCategory.SNACK;
                break;
            case "디저트":
                this.category = FoodCategory.DESSERT;
                break;
            case "음료":
                this.category = FoodCategory.BEVERAGE;
                break;
            default:
                try {
                    this.category = FoodCategory.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    this.category = FoodCategory.KOREAN; // 기본값
                }
        }
    }
}