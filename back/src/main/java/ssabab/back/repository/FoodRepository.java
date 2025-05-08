package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Food;
import ssabab.back.entity.Food.Category;
import ssabab.back.entity.Food.MainSub;
import ssabab.back.entity.Food.Tag;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Integer> {
    Optional<Food> findByFoodNameAndMainSubAndCategoryAndTag(String foodName, MainSub mainSub, Category category, Tag tag);
}
