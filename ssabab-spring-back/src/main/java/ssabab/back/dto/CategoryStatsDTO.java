// dto.CategoryStatsDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsDTO {
    // dm_user_category_stats
    private Long userId;
    private String category;
    private Integer count;
}