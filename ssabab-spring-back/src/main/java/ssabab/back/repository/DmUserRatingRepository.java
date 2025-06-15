// ssabab/back/repository/DmUserRatingRepository.java (새로 추가)
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserRating;

import java.util.Optional;

public interface DmUserRatingRepository extends JpaRepository<DmUserRating, Long> {
    Optional<DmUserRating> findByUserId(Long userId);
}