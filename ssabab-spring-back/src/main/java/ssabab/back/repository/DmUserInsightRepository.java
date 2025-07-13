// repository/DmUserInsightRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.DmUserInsight;
import java.util.Optional;

public interface DmUserInsightRepository extends JpaRepository<DmUserInsight, Long> {
    // 💡 JPQL을 사용하여 ID로 엔티티를 명시적으로 조회합니다.
    @Query("SELECT i FROM DmUserInsight i WHERE i.userId = :userId")
    Optional<DmUserInsight> findByUserId(@Param("userId") Long userId);
}