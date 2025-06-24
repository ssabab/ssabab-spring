package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ssabab.back.entity.Menu;

import java.util.List;

/**
 * 단일 메뉴 응답 DTO (menuId + food 목록)
 */
@Getter
@Setter
@Builder
public class MenuResponseDTO {
    private Long menuId;
    private List<FoodResponseDTO> foods;

    public static MenuResponseDTO from(Menu menu) {
        return MenuResponseDTO.builder()
                .menuId(menu.getMenuId())
                .foods(menu.getFoods().stream().map(FoodResponseDTO::from).toList())
                .build();
    }

    public static MenuResponseDTO empty() {
        return MenuResponseDTO.builder()
                .menuId(null)
                .foods(List.of())
                .build();
    }
}
