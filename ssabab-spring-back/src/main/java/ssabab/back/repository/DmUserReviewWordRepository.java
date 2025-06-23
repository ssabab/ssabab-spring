// repository.DmUserReviewWordRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserReviewWord;
import ssabab.back.entity.DmUserReviewWordId;

import java.util.List;

public interface DmUserReviewWordRepository extends JpaRepository<DmUserReviewWord, DmUserReviewWordId> {
    List<DmUserReviewWord> findByUserId(Long userId);
}