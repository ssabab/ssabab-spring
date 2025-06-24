// repository/DmUserDiversityComparisonRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserDiversityComparison;
import ssabab.back.entity.DmUserDiversityComparisonId;
import ssabab.back.enums.GroupType;

import java.util.List;
import java.util.Optional;

public interface DmUserDiversityComparisonRepository extends JpaRepository<DmUserDiversityComparison, DmUserDiversityComparisonId> {
    // 복합키의 두 필드를 모두 사용하여 특정 레코드를 조회하기 위한 메소드
    Optional<DmUserDiversityComparison> findByUserIdAndGroupType(Long userId, GroupType groupType);

    // 한 사용자의 모든 그룹 타입 비교 데이터를 가져오기 위한 메소드 (필요 시 사용)
    List<DmUserDiversityComparison> findByUserId(Long userId);
}