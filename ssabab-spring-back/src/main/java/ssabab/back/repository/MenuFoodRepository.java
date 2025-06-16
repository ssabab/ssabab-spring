package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.MenuFood;
import ssabab.back.entity.MenuFood.MenuFoodId; // 복합 키 클래스 임포트

public interface MenuFoodRepository extends JpaRepository<MenuFood, MenuFoodId> {
    // 필요한 경우 추가적인 조회/삭제 메서드 정의
}