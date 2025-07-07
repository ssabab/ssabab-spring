// repository/MonthlyFoodRankingRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.MonthlyFoodRanking;
import ssabab.back.entity.MonthlyFoodRankingId;
import java.util.Collection;
import java.util.List;

public interface MonthlyFoodRankingRepository extends JpaRepository<MonthlyFoodRanking, MonthlyFoodRankingId> {

    /**
     * 💡 여러 랭킹 타입(예: "best", "worst")을 한 번의 쿼리로 조회합니다.
     * rankType과 rank 순으로 정렬하여 데이터를 가져옵니다.
     */
    // 💡 [수정] r.rankAsc -> r.rank ASC 로 오타 수정
    @Query("SELECT r FROM MonthlyFoodRanking r WHERE r.rankType IN :rankTypes ORDER BY r.rankType, r.rank ASC")
    List<MonthlyFoodRanking> findByRankTypeInOrderByRankAsc(@Param("rankTypes") Collection<String> rankTypes);
}