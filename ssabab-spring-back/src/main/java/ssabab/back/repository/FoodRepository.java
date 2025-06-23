package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Food;
import ssabab.back.enums.FoodMainSub;
import ssabab.back.enums.FoodCategory;
import ssabab.back.enums.FoodTag;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByFoodNameAndMainSubAndCategoryAndTag(
            String foodName,
            FoodMainSub mainSub,
            FoodCategory category,
            FoodTag tag
    );
}