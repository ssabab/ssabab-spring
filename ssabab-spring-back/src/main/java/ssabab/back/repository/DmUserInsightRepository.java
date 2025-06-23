// repository.DmUserInsightRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserInsight;

public interface DmUserInsightRepository extends JpaRepository<DmUserInsight, Long> {
}