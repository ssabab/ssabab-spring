// entity/DmUserTagStats.java
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dm_user_tag_stats", catalog = "ssabab_dm")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DmUserTagStatsId.class)
public class DmUserTagStats {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "tag", length = 50)
    private String tag;

    @Column(name = "count")
    private Integer count;

}