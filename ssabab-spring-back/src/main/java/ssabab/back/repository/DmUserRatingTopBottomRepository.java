// ssabab/back/repository/DmUserRatingTopBottomRepository.java (새로 추가)
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserRatingTopBottom;

import java.util.Optional;

public interface DmUserRatingTopBottomRepository extends JpaRepository<DmUserRatingTopBottom, Long> {
    Optional<DmUserRatingTopBottom> findByUserId(Long userId);
}