// dto.RatingDataDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDataDTO {
    // dm_user_summary
    private Long userId;
    private Float avgScore;
    private Integer totalReviews;
    private Integer preVoteCount;
}
