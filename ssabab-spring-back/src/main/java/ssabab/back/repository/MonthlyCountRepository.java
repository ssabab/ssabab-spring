// repository.MonthlyCountRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MonthlyCount;

public interface MonthlyCountRepository extends JpaRepository<MonthlyCount, Long> {
}