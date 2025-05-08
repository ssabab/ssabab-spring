package ssabab.back.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodReviewRequestDTO {
    private List<FoodReviewDTO> foods;
}