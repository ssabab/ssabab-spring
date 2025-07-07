// repository/DmUserCategoryStatsRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.DmUserCategoryStats;
import ssabab.back.entity.DmUserCategoryStatsId;
import java.util.List;

public interface DmUserCategoryStatsRepository extends JpaRepository<DmUserCategoryStats, DmUserCategoryStatsId> {
    // 💡 JPQL을 사용하여 엔티티 목록을 명시적으로 조회합니다.
    @Query("SELECT s FROM DmUserCategoryStats s WHERE s.userId = :userId")
    List<DmUserCategoryStats> findByUserId(@Param("userId") Long userId);
}