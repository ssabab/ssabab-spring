package ssabab.back.repository;

import ssabab.back.entity.Account;
import ssabab.back.entity.Food;
import ssabab.back.entity.FoodReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodReviewRepository extends JpaRepository<FoodReview, Integer> {
    List<FoodReview> findByAccount(Account account);
    List<FoodReview> findByFood(Food food);
    Optional<FoodReview> findByAccountAndFood(Account account, Food food);

    @Query("SELECT AVG(fr.foodScore) FROM FoodReview fr WHERE fr.account = :account")
    Double getAverageScoreByUser(Account account);

    @Query("SELECT AVG(fr.foodScore) FROM FoodReview fr")
    Double getGlobalAverageScore();

    @Query("SELECT fr FROM FoodReview fr WHERE fr.account = :account ORDER BY fr.foodScore DESC")
    List<FoodReview> findTopRatedFoodsByUser(Account account);

    @Query("SELECT fr FROM FoodReview fr WHERE fr.account = :account ORDER BY fr.foodScore ASC")
    List<FoodReview> findBottomRatedFoodsByUser(Account account);

    List<FoodReview> findByAccountAndFoodIn(Account account, List<Food> foods);
    void deleteByAccountAndFoodIn(Account account, List<Food> foods);
}