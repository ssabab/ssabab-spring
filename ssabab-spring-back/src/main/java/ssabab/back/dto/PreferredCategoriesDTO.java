// dto.PreferredCategoriesDTO
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PreferredCategoriesDTO{
    private String name;
    private Integer percentage;
}