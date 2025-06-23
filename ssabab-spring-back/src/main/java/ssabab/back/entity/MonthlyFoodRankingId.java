// entity/MonthlyFoodRankingId.java
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
public class MonthlyFoodRankingId implements Serializable {
    private String foodName;
    private String rankType;
    private Integer rank;
}