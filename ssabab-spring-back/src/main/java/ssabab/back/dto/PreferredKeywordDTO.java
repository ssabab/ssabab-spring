// dto.PreferredKeywordDTO
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PreferredKeywordDTO{
    private String value;
    private Integer count;
    private String color;
}