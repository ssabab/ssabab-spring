// repository.MonthlyFrequentEvaluatorRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MonthlyFrequentEvaluator;

import java.util.List;

public interface MonthlyFrequentEvaluatorRepository extends JpaRepository<MonthlyFrequentEvaluator, Integer> {
    List<MonthlyFrequentEvaluator> findAllByOrderByRankAsc();
}