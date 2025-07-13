// repository/MonthlyCountRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MonthlyCount;
import java.util.Collection;
import java.util.List;

public interface MonthlyCountRepository extends JpaRepository<MonthlyCount, Long> {

    /**
     * 💡 여러 달(month ID)의 통계 데이터를 한 번의 쿼리로 조회합니다.
     * Spring Data JPA가 메소드 이름을 기반으로 'SELECT ... WHERE month IN (...) ' 쿼리를 자동으로 생성합니다.
     */
    List<MonthlyCount> findByMonthIn(Collection<Long> months);
}