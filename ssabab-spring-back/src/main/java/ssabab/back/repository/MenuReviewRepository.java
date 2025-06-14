package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MenuReview;
import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

public interface MenuReviewRepository extends JpaRepository<MenuReview, Long> {
    Optional<MenuReview> findByUserUserIdAndMenuMenuId(Long userId, Long menuId);
    List<MenuReview> findByUserUserIdAndMenuDate(Long userId, LocalDate date);
}
