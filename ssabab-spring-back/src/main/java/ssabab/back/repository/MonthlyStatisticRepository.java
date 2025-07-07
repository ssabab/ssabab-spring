// repository/MonthlyStatisticRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MonthlyStatistic;
import java.math.BigDecimal;

public interface MonthlyStatisticRepository extends JpaRepository<MonthlyStatistic, BigDecimal> {
    // 이 테이블은 보통 한 행만 존재하므로, 기본 findAll() 메소드를 사용하는 것이 효율적입니다.
}