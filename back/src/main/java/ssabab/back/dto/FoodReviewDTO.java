package ssabab.back.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodReviewDTO {
    private Integer foodId;
    private String foodName;
    private Integer foodScore;
}