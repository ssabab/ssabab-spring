package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dm_monthly_food_ranking")
@IdClass(DmMonthlyFoodRankingId.class) // 복합 기본 키 클래스 지정
public class DmMonthlyFoodRanking {

    @Id // 복합 키의 첫 번째 부분
    @Column(name = "food_id")
    private Integer foodId; // ERD에 int PK로 명시

    @Id // 복합 키의 두 번째 부분
    @Column(name = "rank_type", length = 255) // ERD에 varchar(255) PK로 명시
    private String rankType;

    @Column(name = "food_name", length = 255) // ERD에 varchar(255)로 명시
    private String foodName;

    @Column(name = "avg_score") // ERD에 decimal(3,2)로 명시
    private Double avgScore; // Java의 Double로 매핑 (Float보다 정밀도 높음)

    @Column(name = "rank")
    private Integer rank; // ERD에 int로 명시

    // 월 컬럼 추가 (ERD에 별도 명시 없지만, 월간 대시보드라면 필요)
    // 이전 ERD 이미지에서 dm_monthly_food_ranking에 month 컬럼이 명시되어 있었음.
    // 누락된 것으로 판단하여 다시 추가.
    @Column(name = "\"month\"") // "month"는 SQL 예약어일 수 있으므로 쿼트 처리
    private Integer month;
}