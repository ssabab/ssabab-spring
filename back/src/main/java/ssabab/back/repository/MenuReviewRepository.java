package ssabab.back.repository;

import ssabab.back.entity.Account;
import ssabab.back.entity.Menu;
import ssabab.back.entity.MenuReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuReviewRepository extends JpaRepository<MenuReview, Integer> {
    List<MenuReview> findByAccount(Account account);
    List<MenuReview> findByMenu(Menu menu);
    Optional<MenuReview> findByAccountAndMenu(Account account, Menu menu);

    @Query("SELECT AVG(m.menuScore) FROM MenuReview m WHERE m.menu = :menu")
    Double getAverageScoreByMenu(@Param("menu") Menu menu);
}