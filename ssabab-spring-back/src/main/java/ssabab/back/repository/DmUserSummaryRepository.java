// repository.DmUserSummaryRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserSummary;

public interface DmUserSummaryRepository extends JpaRepository<DmUserSummary, Long> {
}