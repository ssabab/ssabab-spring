package ssabab.back.repository;

import ssabab.back.entity.Food;
import ssabab.back.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    Optional<Food> findByFoodName(String foodName);

    @Query("SELECT f FROM Food f WHERE f.foodName = :foodName AND f.category = :category")
    Optional<Food> findByFoodNameAndCategory(@Param("foodName") String foodName, @Param("category") FoodCategory category);

    @Query(value = "SELECT f.* FROM food f JOIN menu_food mf ON f.food_id = mf.food_id " +
            "JOIN food_review fr ON f.food_id = fr.food_id " +
            "GROUP BY f.food_id ORDER BY AVG(fr.food_score) DESC LIMIT 5", nativeQuery = true)
    List<Food> findTopRatedFoods();

    @Query(value = "SELECT f.* FROM food f JOIN menu_food mf ON f.food_id = mf.food_id " +
            "JOIN food_review fr ON f.food_id = fr.food_id " +
            "GROUP BY f.food_id ORDER BY AVG(fr.food_score) ASC LIMIT 5", nativeQuery = true)
    List<Food> findBottomRatedFoods();
}
