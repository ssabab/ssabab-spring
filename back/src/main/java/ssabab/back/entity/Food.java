package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer foodId;

    private String foodName;

    @Enumerated(EnumType.STRING)
    private MainSub mainSub;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Tag tag;

    @ManyToMany(mappedBy = "foods")
    private List<Menu> menus;

    public enum MainSub { 주메뉴, 서브메뉴, 일반메뉴 }
    public enum Category { 한식, 중식, 일식, 양식 }
    public enum Tag { 고기, 야채, 생선, 기타, 국물, 밥 }
}