package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssabab.back.enums.FoodMainSub;
import ssabab.back.enums.FoodCategory;
import ssabab.back.enums.FoodTag;

import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * 음식 엔티티 - 개별 음식 항목 (메뉴 구성 요소)
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "food")
@Data
@NoArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long foodId;

    @Column(name = "food_name", length = 100)
    private String foodName;   // 음식 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "main_sub")
    private FoodMainSub mainSub;    // 중요도 (메인메뉴, 서브메뉴, 일반메뉴)

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private FoodCategory category;   // 음식 분류 (한식, 중식, 일식, 양식)

    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private FoodTag tag;        // 음식 특성 태그 (밥, 면, 국, 생선, 고기, 야채, 기타)
}
