// entity.MonthlyCount.java
package ssabab.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dm_monthly_count") // 테이블명 변경
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyCount {

    @Id
    @Column(name = "month") // 월을 ID로 사용
    private Long month;

    @Column(name = "evaluator")
    private Long evaluator; // 월별 평가자 수

    @Column(name = "cumulative")
    private Long cumulative; // 누적 평가 수

    @Column(name = "difference")
    private Long difference; // 전월 대비 증가분
}