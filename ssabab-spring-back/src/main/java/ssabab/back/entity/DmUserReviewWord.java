// entity/DmUserReviewWord.java
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dm_user_review_word")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DmUserReviewWordId.class) // IdClass를 사용한다고 명시
public class DmUserReviewWord {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "word")
    private String word;

    @Column(name = "count")
    private Integer count;

    @Column(name = "sentiment_score")
    private Float sentimentScore;
}