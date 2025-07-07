// repository/DmUserFoodRatingRankRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.DmUserFoodRatingRank;
import ssabab.back.entity.DmUserFoodRatingRankId;
import java.util.List;

public interface DmUserFoodRatingRankRepository extends JpaRepository<DmUserFoodRatingRank, DmUserFoodRatingRankId> {
    // 💡 JPQL을 사용하여 엔티티 자체(DmUserFoodRatingRank)를 조회합니다.
    @Query("SELECT r FROM DmUserFoodRatingRank r WHERE r.userId = :userId")
    List<DmUserFoodRatingRank> findByUserId(@Param("userId") Long userId);
}