// repository/DmUserTagStatsRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.DmUserTagStats;
import ssabab.back.entity.DmUserTagStatsId;
import java.util.List;

public interface DmUserTagStatsRepository extends JpaRepository<DmUserTagStats, DmUserTagStatsId> {
    // 💡 JPQL을 사용하여 엔티티 목록을 명시적으로 조회합니다.
    @Query("SELECT s FROM DmUserTagStats s WHERE s.userId = :userId")
    List<DmUserTagStats> findByUserId(@Param("userId") Long userId);
}