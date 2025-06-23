// entity/DmUserDiversityComparisonId.java
package ssabab.back.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import ssabab.back.enums.GroupType;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DmUserDiversityComparisonId implements Serializable {
    private Long userId;
    @Enumerated(EnumType.STRING)
    private GroupType groupType;
}