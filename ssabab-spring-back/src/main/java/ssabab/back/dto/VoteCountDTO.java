// ssabab/back/dto/VoteCountDto.java (월간 대시보드에서 사용)
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteCountDTO {
    private Long menuId; // ERD에 없으나, 메뉴별 투표수라면 필요할 수 있음. 임시 null
    private String menuName; // 예: "A메뉴", "B메뉴"
    private List<FoodResponseDTO> foods; // 해당 메뉴의 음식 목록
    private Long voteCount;
}