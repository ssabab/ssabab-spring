// repository/DmUserDiversityComparisonRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.DmUserDiversityComparison;
import ssabab.back.entity.DmUserDiversityComparisonId;
import ssabab.back.enums.GroupType;
import java.util.Optional;

public interface DmUserDiversityComparisonRepository extends JpaRepository<DmUserDiversityComparison, DmUserDiversityComparisonId> {
    // 💡 JPQL을 사용하여 userId와 groupType으로 엔티티를 명시적으로 조회합니다.
    @Query("SELECT c FROM DmUserDiversityComparison c WHERE c.userId = :userId AND c.groupType = :groupType")
    Optional<DmUserDiversityComparison> findByUserIdAndGroupType(@Param("userId") Long userId, @Param("groupType") GroupType groupType);
}