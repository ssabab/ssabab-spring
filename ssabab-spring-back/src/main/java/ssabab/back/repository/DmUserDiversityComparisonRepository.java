// repository/DmUserDiversityComparisonRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserDiversityComparison;
import ssabab.back.entity.DmUserDiversityComparisonId;
import ssabab.back.enums.GroupType; // 새로 만든 enum을 import

import java.util.Optional;

public interface DmUserDiversityComparisonRepository extends JpaRepository<DmUserDiversityComparison, DmUserDiversityComparisonId> {
    // String 대신 GroupType enum을 직접 사용하고, 복합키 필드 조회 규칙에 따라 메소드 이름 변경
    Optional<DmUserDiversityComparison> findByUserIdAndGroupType(Long userId, GroupType groupType);
}