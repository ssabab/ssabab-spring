// repository/DmUserReviewWordRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserReviewWord;
import ssabab.back.entity.DmUserReviewWordId;

import java.util.List;

public interface DmUserReviewWordRepository extends JpaRepository<DmUserReviewWord, DmUserReviewWordId> {
    // 복합키의 일부인 userId로 조회하기 위한 메소드
    List<DmUserReviewWord> findByUserId(Long userId);
}