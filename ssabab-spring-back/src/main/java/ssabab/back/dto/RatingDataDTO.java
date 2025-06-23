// dto.RatingDataDTO
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDataDTO {
    private Double averageRating;
    private Integer totalReviews;
}
