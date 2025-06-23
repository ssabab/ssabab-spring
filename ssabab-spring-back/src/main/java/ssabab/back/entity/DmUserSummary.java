// entity.DmUserSummary.java
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dm_user_summary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmUserSummary {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "avg_score")
    private Float avgScore;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @Column(name = "pre_vote_count")
    private Integer preVoteCount;
}