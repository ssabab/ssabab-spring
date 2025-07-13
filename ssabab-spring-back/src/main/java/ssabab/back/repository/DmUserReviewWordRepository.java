// repository/DmUserReviewWordRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.DmUserReviewWord;
import ssabab.back.entity.DmUserReviewWordId;
import java.util.List;

public interface DmUserReviewWordRepository extends JpaRepository<DmUserReviewWord, DmUserReviewWordId> {
    // 💡 JPQL을 사용하여 엔티티 목록을 명시적으로 조회합니다.
    @Query("SELECT w FROM DmUserReviewWord w WHERE w.userId = :userId")
    List<DmUserReviewWord> findByUserId(@Param("userId") Long userId);
}