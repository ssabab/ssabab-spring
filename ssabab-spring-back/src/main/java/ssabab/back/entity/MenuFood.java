package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable; // 복합 키를 위해 Serializable 구현

@Entity
@Table(name = "menu_food")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuFood {

    // 복합 키 클래스 정의
    @Embeddable // 임베디드 가능한 ID 클래스임을 나타냄
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuFoodId implements Serializable {
        private Long menuId; // Menu 엔티티의 ID와 매핑
        private Long foodId; // Food 엔티티의 ID와 매핑
    }

    @EmbeddedId // 복합 키 사용
    private MenuFoodId id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @MapsId("menuId") // 복합 키의 menuId 부분과 매핑
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @MapsId("foodId") // 복합 키의 foodId 부분과 매핑
    @JoinColumn(name = "food_id")
    private Food food;

    // 편의 메서드 (엔티티 생성 시 사용)
    public MenuFood(Menu menu, Food food) {
        this.menu = menu;
        this.food = food;
        this.id = new MenuFoodId(menu.getMenuId(), food.getFoodId());
    }
}