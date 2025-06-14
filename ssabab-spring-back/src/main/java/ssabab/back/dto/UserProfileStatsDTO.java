// ssabab/back/dto/UserProfileStatsDto.java (월간/일간 대시보드에서 사용)
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileStatsDTO {
    private String type; // 예: "ssafyYear", "classNum", "gender", "age"
    private String value; // 예: "10기", "1반", "남성", "20대"
    private Long count; // 해당 값의 사용자 수
    private Double percentage; // 전체 대비 비율
}