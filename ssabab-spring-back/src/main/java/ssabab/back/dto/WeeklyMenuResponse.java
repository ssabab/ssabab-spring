// dto.WeeklyMenuResponse
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * 특정 기간 동안의 모든 DailyMenuResponse를 포함하는 응답 DTO
 */
@Getter
@Builder
public class WeeklyMenuResponse {
    private final List<DailyMenuResponse> weeklyMenus;
}