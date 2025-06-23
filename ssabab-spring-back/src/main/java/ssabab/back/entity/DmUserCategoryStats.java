// entity/DmUserCategoryStats.java
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dm_user_category_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DmUserCategoryStatsId.class) // IdClass를 사용한다고 명시
public class DmUserCategoryStats {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "category")
    private String category;

    @Column(name = "count")
    private Integer count;

    @Column(name = "ratio")
    private Float ratio;
}