// ssabab/back/dto/KeywordCountDto.java (Daily Dashboard용, 하지만 현재 Daily 제외)
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordCountDTO {
    private String keyword;
    private Long count;
}