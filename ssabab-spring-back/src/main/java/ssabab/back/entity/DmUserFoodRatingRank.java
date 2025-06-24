// entity/DmUserFoodRatingRank.java
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssabab.back.enums.ScoreType;

@Entity
@Table(name = "dm_user_food_rating_rank",catalog = "ssabab_dm")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DmUserFoodRatingRankId.class) // IdClass를 사용한다고 명시
public class DmUserFoodRatingRank {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "score_type")
    @Enumerated(EnumType.STRING)
    private ScoreType scoreType;

    @Id
    @Column(name = "rank_order")
    private Integer rankOrder;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "food_score")
    private Float foodScore;
}