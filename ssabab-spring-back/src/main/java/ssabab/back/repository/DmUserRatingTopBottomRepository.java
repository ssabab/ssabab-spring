// repository.DmUserRatingTopBottomRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserRatingTopBottom;

import java.util.Optional;

public interface DmUserRatingTopBottomRepository extends JpaRepository<DmUserRatingTopBottom, Long> {
    Optional<DmUserRatingTopBottom> findByUserId(Long userId);
}