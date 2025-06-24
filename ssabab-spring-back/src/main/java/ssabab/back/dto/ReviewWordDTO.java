// dto.ReviewWordDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewWordDTO {
    // dm_user_review_word
    private Long userId;
    private String word;
    private Integer count;
}