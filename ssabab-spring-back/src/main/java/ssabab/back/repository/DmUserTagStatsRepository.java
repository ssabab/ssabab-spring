// repository/DmUserTagStatsRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserTagStats;
import ssabab.back.entity.DmUserTagStatsId;

import java.util.List;

public interface DmUserTagStatsRepository extends JpaRepository<DmUserTagStats, DmUserTagStatsId> {
    // userId로 태그 통계 목록을 조회하는 메소드
    List<DmUserTagStats> findByUserId(Long userId);
}