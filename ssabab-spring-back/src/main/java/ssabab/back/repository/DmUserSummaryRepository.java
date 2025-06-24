// repository/DmUserSummaryRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserSummary;

public interface DmUserSummaryRepository extends JpaRepository<DmUserSummary, Long> {
    // JpaRepository가 기본적으로 findById(Long id)를 제공하므로 추가 메소드 필요 없음
}