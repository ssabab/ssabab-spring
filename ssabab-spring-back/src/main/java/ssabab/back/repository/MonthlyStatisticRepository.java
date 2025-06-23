// repository.MonthlyStatisticRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MonthlyStatistic;
import java.math.BigDecimal;

public interface MonthlyStatisticRepository extends JpaRepository<MonthlyStatistic, BigDecimal> {
}