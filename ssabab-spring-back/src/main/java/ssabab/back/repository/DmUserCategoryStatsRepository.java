// repository.DmUserCategoryStatsRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserCategoryStats;
import ssabab.back.entity.DmUserCategoryStatsId;

import java.util.List;

public interface DmUserCategoryStatsRepository extends JpaRepository<DmUserCategoryStats, DmUserCategoryStatsId> {
    List<DmUserCategoryStats> findByUserId(Long userId);
}