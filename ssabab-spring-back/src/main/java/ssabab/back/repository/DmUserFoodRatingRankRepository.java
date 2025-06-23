// repository.DmUserFoodRatingRankRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserFoodRatingRank;
import ssabab.back.entity.DmUserFoodRatingRankId;

import java.util.List;

public interface DmUserFoodRatingRankRepository extends JpaRepository<DmUserFoodRatingRank, DmUserFoodRatingRankId> {
    List<DmUserFoodRatingRank> findByUserId(Long userId);
}