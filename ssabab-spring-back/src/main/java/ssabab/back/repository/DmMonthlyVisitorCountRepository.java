// ssabab/back/repository/DmMonthlyVisitorCountRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmMonthlyVisitorCount;

import java.util.Optional;

public interface DmMonthlyVisitorCountRepository extends JpaRepository<DmMonthlyVisitorCount, Long> {
    Optional<DmMonthlyVisitorCount> findByMonth(Integer month);
}