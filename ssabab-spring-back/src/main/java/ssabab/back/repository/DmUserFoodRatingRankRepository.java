// repository/DmUserFoodRatingRankRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserFoodRatingRank;
import ssabab.back.entity.DmUserFoodRatingRankId;

import java.util.List;

public interface DmUserFoodRatingRankRepository extends JpaRepository<DmUserFoodRatingRank, DmUserFoodRatingRankId> {
    // 복합키의 일부인 userId로 조회하기 위한 메소드
    List<DmUserFoodRatingRank> findByUserId(Long userId);
}