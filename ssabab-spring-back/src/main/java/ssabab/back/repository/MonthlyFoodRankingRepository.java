// repository.MonthlyFoodRankingRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MonthlyFoodRanking;
import ssabab.back.entity.MonthlyFoodRankingId;

import java.util.List;

public interface MonthlyFoodRankingRepository extends JpaRepository<MonthlyFoodRanking, MonthlyFoodRankingId> {
    List<MonthlyFoodRanking> findByRankTypeOrderByRankAsc(String rankType);
}