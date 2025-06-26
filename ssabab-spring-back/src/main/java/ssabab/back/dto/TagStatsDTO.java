// dto/TagStatsDTO.java
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagStatsDTO {
    private Long userId;
    private String tag;
    private Integer count;
}