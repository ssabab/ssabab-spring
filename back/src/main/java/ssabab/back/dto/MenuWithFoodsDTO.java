package ssabab.back.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuWithFoodsDTO {
    private Integer menuId;
    private List<FoodDTO> foods;
}