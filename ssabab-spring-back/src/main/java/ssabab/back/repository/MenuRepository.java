package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Menu;
import java.util.List;
import java.time.LocalDate;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByDate(LocalDate date);
}
