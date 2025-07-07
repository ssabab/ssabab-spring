// repository.MenuReviewRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MenuReview;
import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

public interface MenuReviewRepository extends JpaRepository<MenuReview, Long> {
    Optional<MenuReview> findByUserUserIdAndMenuMenuId(Long userId, Long menuId);

    // 특정 사용자가 특정 날짜에 작성한 모든 메뉴 리뷰를 조회 (기존 메소드 활용)
    List<MenuReview> findByUserUserIdAndMenuDate(Long userId, LocalDate date);
}