// dto.UserPreVoteResponseDTO.java
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 현재 사용자의 특정 날짜 사전투표 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PreVoteResponseDTO {
    /**
     * 사용자가 투표한 메뉴의 ID.
     * 투표 내역이 없으면 null이 반환됩니다.
     */
    private Long menuId;
}