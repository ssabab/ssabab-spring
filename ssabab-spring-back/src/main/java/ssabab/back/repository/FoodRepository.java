package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
