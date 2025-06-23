// entity/DmUserFoodRatingRankId.java
package ssabab.back.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ssabab.back.enums.ScoreType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DmUserFoodRatingRankId implements Serializable {
    private Long userId;
    @Enumerated(EnumType.STRING)
    private ScoreType scoreType;
    private Integer rankOrder;
}