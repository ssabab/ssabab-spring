// dto.FriendPreVoteResponseDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

/**
 * 친구의 사전 투표 결과 응답 DTO
 */
@Getter
@Builder
public class FriendPreVoteResponseDTO {
    private final Long friendId;
    private final String friendName;
    private final Long votedMenuId;
    private final List<FoodInfo> votedMenuInfo;
    private final LocalDate votedMenuDate;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FoodInfo {
        private final Long foodId;
        private final String foodName;
    }
}
