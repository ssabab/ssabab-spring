// repository.MenuRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // @Query 임포트 추가
import org.springframework.data.repository.query.Param; // @Param 임포트 추가
import ssabab.back.entity.Menu;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    // 특정 날짜의 메뉴 목록을 조회하고, 관련된 MenuFood와 Food를 JOIN FETCH로 함께 가져옵니다.
    // MenuFood의 food 필드를 통해 Food 엔티티까지 탐색하여 가져오도록 합니다.
    @Query("SELECT DISTINCT m FROM Menu m " +
            "LEFT JOIN FETCH m.menuFoods mf " + // Menu와 MenuFood를 조인하여 함께 페치
            "LEFT JOIN FETCH mf.food f " +      // MenuFood와 Food를 조인하여 함께 페치
            "WHERE m.date = :date " +
            "ORDER BY m.menuId ASC")
    List<Menu> findMenusWithFoodsByDate(@Param("date") LocalDate date);

    // 특정 날짜 범위 내의 모든 메뉴를 조회하고, 관련된 MenuFood와 Food를 JOIN FETCH로 함께 가져옵니다.
    @Query("SELECT DISTINCT m FROM Menu m " +
            "LEFT JOIN FETCH m.menuFoods mf " +
            "LEFT JOIN FETCH mf.food f " +
            "WHERE m.date BETWEEN :startDate AND :endDate " +
            "ORDER BY m.date ASC, m.menuId ASC")
    List<Menu> findMenusWithFoodsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 기존 findByDate도 JOIN FETCH를 적용하여 N+1 문제 방지 (관리자 기능에서 사용)
    @Query("SELECT DISTINCT m FROM Menu m " +
            "LEFT JOIN FETCH m.menuFoods mf " +
            "LEFT JOIN FETCH mf.food f " +
            "WHERE m.date = :date")
    List<Menu> findMenusWithFoodsByDateForAdmin(@Param("date") LocalDate date);

    // findById는 기본 JPA 메서드이므로, 필요에 따라 @EntityGraph를 사용하거나
    // 해당 ID로 메뉴를 가져올 때도 Food 정보를 함께 가져오려면 별도의 @Query를 작성할 수 있습니다.
    // 예:
    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.menuFoods mf LEFT JOIN FETCH mf.food f WHERE m.menuId = :menuId")
    Optional<Menu> findByIdWithFoods(@Param("menuId") Long menuId);

    // saveDailyMenus에서 사용되는 findByDate는 위 findMenusWithFoodsByDateForAdmin으로 대체
    // 여기서는 단순히 Menu 객체만 필요한 경우가 있을 수 있으므로, 이 메서드 자체는 남겨둘 수 있으나,
    // 성능이 중요하다면 findMenusWithFoodsByDateForAdmin을 사용하는 것이 좋습니다.
    // List<Menu> findByDate(LocalDate date);
}