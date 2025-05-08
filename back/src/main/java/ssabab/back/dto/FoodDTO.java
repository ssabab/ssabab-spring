package ssabab.back.dto;

import lombok.*;
import ssabab.back.entity.Food;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodDTO {
    private Integer foodId;
    private String foodName;
    private Food.MainSub mainSub;
    private Food.Category category;
    private Food.Tag tag;
}