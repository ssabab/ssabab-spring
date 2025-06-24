// dto.UserGroupComparisonDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupComparisonDTO {
    private Long userId;
    private String groupType;
    private Float userAvgScore;
    private Float userDiversityScore;
    private Float groupAvgScore;
    private Float groupDiversityScore;
}