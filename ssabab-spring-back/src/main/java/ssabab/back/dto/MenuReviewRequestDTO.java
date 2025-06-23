// dto.MenuReviewRequestDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 메뉴 리뷰 요청 DTO - 후회 여부 및 한 줄 평 등록용
 */
@Getter
@Builder
@Setter
public class MenuReviewRequestDTO {
    private final Long menuId;
    private final Boolean menuRegret;
    private final String menuComment;
    private final Double menuScore;
}
