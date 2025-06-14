package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * 식단 메뉴 엔티티 - 특정 날짜의 전체 메뉴 구성을 표현
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "date")
    private LocalDate date;  // 식단 날짜

    @ManyToMany
    @JoinTable(name = "menu_food",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id"))
    private List<Food> foods = new ArrayList<>();  // 해당 메뉴에 포함된 Food 목록
}
