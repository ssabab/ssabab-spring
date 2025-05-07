package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
}
