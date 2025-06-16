package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Menu;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    // 특정 날짜의 모든 메뉴를 menuId 오름차순으로 정렬하여 가져옴 (menu1/menu2 구분 기준, 불안정)
    List<Menu> findByDateOrderByMenuIdAsc(LocalDate date);

    // 특정 날짜 범위 내의 모든 메뉴를 날짜, menuId 오름차순으로 정렬하여 조회
    List<Menu> findByDateBetweenOrderByDateAscMenuIdAsc(LocalDate startDate, LocalDate endDate);

    // 기존 findByDate (메뉴가 2개 미만일 때 체크용 등)
    List<Menu> findByDate(LocalDate date);
}