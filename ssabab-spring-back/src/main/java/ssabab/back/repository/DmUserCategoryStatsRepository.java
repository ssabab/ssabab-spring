// repository/DmUserCategoryStatsRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserCategoryStats;
import ssabab.back.entity.DmUserCategoryStatsId;

import java.util.List;

public interface DmUserCategoryStatsRepository extends JpaRepository<DmUserCategoryStats, DmUserCategoryStatsId> {
    // 복합키의 일부인 userId로 조회하기 위한 메소드
    List<DmUserCategoryStats> findByUserId(Long userId);
}