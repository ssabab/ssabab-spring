package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import ssabab.back.entity.Menu;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 특정 날짜의 메뉴 정보 DTO (응답용)
 */
@Getter
@Builder
public class MenuResponseDTO {
    private final Long menuId;
    private final LocalDate date;
    private final List<FoodResponseDTO> foods;

    public static MenuResponseDTO from(Menu menu) {
        return MenuResponseDTO.builder()
            .menuId(menu.getMenuId())
            .date(menu.getDate())
            .foods(menu.getFoods().stream()
                .map(FoodResponseDTO::from)
                .collect(Collectors.toList()))
            .build();
    }
}
