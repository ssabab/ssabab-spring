// dto.DailyMenuResponse
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

/**
 * 특정 날짜의 두 가지 메뉴 (menu1, menu2)를 포함하는 최종 응답 DTO
 */
@Getter
@Builder
public class DailyMenuResponse {
    private final LocalDate date;
    private final MenuResponseDTO menu1; // 첫 번째 메뉴
    private final MenuResponseDTO menu2; // 두 번째 메뉴
}