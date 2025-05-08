package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.FoodReview;
import ssabab.back.entity.Account;
import ssabab.back.entity.Food;

import java.util.List;

public interface FoodReviewRepository extends JpaRepository<FoodReview, Integer> {
    List<FoodReview> findByAccountAndFoodIn(Account account, List<Food> foods);
    void deleteByAccountAndFoodIn(Account account, List<Food> foods);
}