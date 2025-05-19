package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer foodId;

    private String foodName;
    
    private String mainSub; // 주메뉴 또는 서브메뉴

    @Enumerated(EnumType.STRING)
    private FoodCategory category;

    private String tag; // 태그

    @Enumerated(EnumType.STRING)
    private Taste taste;

    @Enumerated(EnumType.STRING)
    private SpicyLevel spicyLevel;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuFood> menuFoods = new ArrayList<>();
}