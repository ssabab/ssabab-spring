package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import ssabab.back.entity.PreVote;

/**
 * 사전 투표 요청 DTO - 등록 및 수정 요청 시 사용
 */
@Getter
@Builder
public class PreVoteRequestDTO {
    private final Long preVoteId;
    private final Long userId;
    private final Long menuId;

    public static PreVoteRequestDTO from(PreVote preVote) {
        return PreVoteRequestDTO.builder()
            .preVoteId(preVote.getPreVoteId())
            .userId(preVote.getUser().getUserId())
            .menuId(preVote.getMenu().getMenuId())
            .build();
    }
}
