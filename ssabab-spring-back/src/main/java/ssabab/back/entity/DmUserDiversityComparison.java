// entity/DmUserDiversityComparison.java
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssabab.back.enums.GroupType;


@Entity
@Table(name = "dm_user_group_comparison",catalog = "ssabab_dm")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DmUserDiversityComparisonId.class) // IdClass를 사용한다고 명시
public class DmUserDiversityComparison {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "group_type")
    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    @Column(name = "user_avg_score")
    private Float userAvgScore;

    @Column(name = "group_avg_score")
    private Float groupAvgScore;

    @Column(name = "user_diversity_score")
    private Float userDiversityScore;

    @Column(name = "group_diversity_score")
    private Float groupDiversityScore;
}