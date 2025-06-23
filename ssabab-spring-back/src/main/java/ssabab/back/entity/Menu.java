// dto.Menu
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * 식단 메뉴 엔티티 - 특정 날짜의 전체 메뉴 구성을 표현
 * (menuOrder 필드 없음 - menu_id 순서로 menu1/menu2 구분, 불안정)
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "menu") // menuOrder가 없으므로 uniqueConstraints 속성 없음
@Data
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "date", nullable = false)
    private LocalDate date;  // 식단 날짜

    // private int menuOrder; // 변경: menuOrder 필드 제거됨

    // 변경: ManyToMany 대신 MenuFood 중간 엔티티를 통한 OneToMany 관계
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuFood> menuFoods = new ArrayList<>();  // MenuFood 연결 엔티티 목록

    // 편의 메서드: Menu에서 직접 Food 목록을 가져오는 Getter (프론트엔드 응답 DTO에서 사용)
    @Transient // DB에 매핑되지 않는 필드
    public List<Food> getFoods() {
        return menuFoods.stream()
                .map(MenuFood::getFood)
                .collect(Collectors.toList());
    }

    // 편의 메서드: Menu에 Food 목록을 설정하는 Setter (서비스에서 사용)
    public void setFoods(List<Food> foods) {
        this.menuFoods.clear(); // 기존 MenuFood 연결 삭제 (orphanRemoval=true 작동)
        if (foods != null) {
            for (Food food : foods) {
                // MenuFood 객체 생성 시 현재 Menu 엔티티와 Food 엔티티를 연결
                this.menuFoods.add(new MenuFood(this, food));
            }
        }
    }
}