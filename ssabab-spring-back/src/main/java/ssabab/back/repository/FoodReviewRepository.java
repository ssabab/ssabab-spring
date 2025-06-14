package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.FoodReview;
import java.util.Optional;

public interface FoodReviewRepository extends JpaRepository<FoodReview, Long> {
    Optional<FoodReview> findByUserUserIdAndFoodFoodId(Long userId, Long foodId);
}
