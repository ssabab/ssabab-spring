// repository.DmMonthlyFoodRankingRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmMonthlyFoodRanking;
import ssabab.back.entity.DmMonthlyFoodRankingId; // 복합 키 ID 클래스 임포트

import java.util.List;

// JpaRepository<엔티티 타입, 엔티티의 PK 타입>
public interface DmMonthlyFoodRankingRepository extends JpaRepository<DmMonthlyFoodRanking, DmMonthlyFoodRankingId> {
    List<DmMonthlyFoodRanking> findByMonthAndRankTypeOrderByRankAsc(Integer month, String rankType);
}