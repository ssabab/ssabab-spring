// entity.DmUserInsight.java
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dm_user_insight")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmUserInsight {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "insight", columnDefinition = "TEXT")
    private String insight;
}