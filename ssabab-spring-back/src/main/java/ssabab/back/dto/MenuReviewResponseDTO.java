// dto.MenuReviewResponseDTO.java
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 현재 사용자의 특정 날짜 메뉴 리뷰 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuReviewResponseDTO {
    /**
     * 리뷰가 작성된 메뉴의 ID
     */
    private Long menuId;
}