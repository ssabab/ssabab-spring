// dto.UserInsightDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInsightDTO {
    // dm_user_insight
    private Long userId;
    private String insight;
}