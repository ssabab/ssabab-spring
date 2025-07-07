// repository/MonthlyFrequentEvaluatorRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MonthlyFrequentEvaluator;
import java.util.List;

public interface MonthlyFrequentEvaluatorRepository extends JpaRepository<MonthlyFrequentEvaluator, Integer> {

    /**
     * 💡 모든 평가왕 목록을 순위(rank) 오름차순으로 조회합니다.
     * Spring Data JPA가 메소드 이름을 기반으로 'SELECT ... ORDER BY rank ASC' 쿼리를 자동으로 생성합니다.
     */
    List<MonthlyFrequentEvaluator> findAllByOrderByRankAsc();
}