// entity/DmUserTagStatsId.java
package ssabab.back.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DmUserTagStatsId implements Serializable {
    private Long userId;
    private String tag;
}