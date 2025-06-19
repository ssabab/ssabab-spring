package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import ssabab.back.entity.Menu; // Menu 엔티티 임포트 유지
import java.time.LocalDate; // LocalDate 임포트 유지 (from 메서드 파라미터로 인해)
import java.util.List;
import java.util.stream.Collectors;

/**
 * 특정 메뉴 (Menu1 또는 Menu2)의 상세 정보 DTO (응답용)
 */
@Getter
@Builder
public class MenuResponseDTO {

    private final List<FoodResponseDTO> foods;

    public static MenuResponseDTO from(Menu menu) {
        return MenuResponseDTO.builder()
                .foods(menu.getFoods().stream()
                        .map(FoodResponseDTO::from)
                        .collect(Collectors.toList()))
                .build();
    }

    public static MenuResponseDTO empty() {
        return MenuResponseDTO.builder()
                .foods(List.of()) // 빈 리스트
                .build();
    }
}