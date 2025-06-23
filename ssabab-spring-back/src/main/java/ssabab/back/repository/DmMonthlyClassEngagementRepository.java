// repository.DmMonthlyClassEngagementRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmMonthlyClassEngagement;

import java.util.List;

public interface DmMonthlyClassEngagementRepository extends JpaRepository<DmMonthlyClassEngagement, Long> {
    List<DmMonthlyClassEngagement> findByMonth(Integer month);
}