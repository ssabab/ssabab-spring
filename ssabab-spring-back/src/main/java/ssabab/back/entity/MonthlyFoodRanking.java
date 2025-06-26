// entity/MonthlyFoodRanking.java
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "monthly_food_ranking",catalog = "ssabab_dm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MonthlyFoodRankingId.class) // IdClass를 사용한다고 명시
public class MonthlyFoodRanking {

    @Id
    @Column(name = "food_name", length = 255)
    private String foodName;

    @Id
    @Column(name = "rank_type", length = 50)
    private String rankType;

    @Id
    @Column(name = "rank")
    private Integer rank;

    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "avg_score", precision = 3, scale = 2)
    private BigDecimal avgScore;

    @Column(name = "count")
    private Long count;
}