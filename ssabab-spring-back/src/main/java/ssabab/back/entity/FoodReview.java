package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * 음식 평점 엔티티 - 사용자가 특정 음식에 남긴 평점을 표현
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "food_review")
@Data
@NoArgsConstructor
public class FoodReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long foodReviewId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "food_score")
    private Long foodScore;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
