// repository.MenuReviewRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ssabab.back.entity.MenuReview;
import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

public interface MenuReviewRepository extends JpaRepository<MenuReview, Long> {
    Optional<MenuReview> findByUserUserIdAndMenuMenuId(Long userId, Long menuId);

    List<MenuReview> findByUserUserIdAndMenuDate(Long userId, LocalDate date);

    // 수정된 쿼리: MenuReview -> User, Menu -> MenuFood -> Food 조인
    @Query("SELECT mr FROM MenuReview mr JOIN FETCH mr.user u JOIN FETCH mr.menu m LEFT JOIN FETCH m.menuFoods mf LEFT JOIN FETCH mf.food f WHERE u.userId IN :userIds AND m.date = :date")
    List<MenuReview> findByUserUserIdInAndMenuDateWithMenuAndFoods(@Param("userIds") List<Long> userIds, @Param("date") LocalDate date);
}