// repository/DmUserSummaryRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.DmUserSummary;
import java.util.Optional;

public interface DmUserSummaryRepository extends JpaRepository<DmUserSummary, Long> {
    // 💡 JPQL을 사용하여 ID로 엔티티를 명시적으로 조회합니다.
    @Query("SELECT s FROM DmUserSummary s WHERE s.userId = :userId")
    Optional<DmUserSummary> findByUserId(@Param("userId") Long userId);
}