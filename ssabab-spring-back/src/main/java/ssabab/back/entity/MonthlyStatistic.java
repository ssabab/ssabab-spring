// entity.MonthlyStatistic.java
package ssabab.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "dm_monthly_statistic") // 테이블명 변경
@Data
@NoArgsConstructor
public class MonthlyStatistic {
    @Id
    @Column(name = "avg") // avg를 pk로 임시 사용, 실제로는 이 테이블은 row가 1개여야 함
    private BigDecimal avg;

    @Column(name = "min")
    private Integer min;

    @Column(name = "max")
    private Integer max;

    @Column(name = "q1")
    private Integer q1;

    @Column(name = "q3")
    private Integer q3;

    @Column(name = "variance", precision = 3, scale = 2)
    private BigDecimal variance;

    @Column(name = "stdev", precision = 3, scale = 2)
    private BigDecimal stdev;
}