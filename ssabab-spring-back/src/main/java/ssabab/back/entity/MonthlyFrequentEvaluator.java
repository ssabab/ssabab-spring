// entity.MonthlyFrequentEvaluator.java
package ssabab.back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "monthly_frequent_evaluator",catalog = "ssabab_dm") // 테이블명 변경
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyFrequentEvaluator {

    @Id
    @Column(name = "rank")
    private Integer rank;

    @Column(name = "name")
    private String name;

    @Column(name = "evaluates")
    private Long evaluates;

    @Column(name = "last_evaluate")
    private Timestamp lastEvaluate;
}