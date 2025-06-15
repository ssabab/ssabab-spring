// ssabab/back/entity/DmMonthlyFoodRankingId.java
package ssabab.back.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * dm_monthly_food_ranking 테이블의 복합 기본 키를 위한 ID 클래스
 * food_id와 rank_type이 복합 키를 이룹니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DmMonthlyFoodRankingId implements Serializable {
    private static final long serialVersionUID = 1L; // Serializable을 위한 버전 UID

    private Integer foodId; // ERD에 int PK로 명시되어 있으므로 Integer 타입
    private String rankType; // ERD에 varchar(255) PK로 명시되어 있으므로 String 타입
}