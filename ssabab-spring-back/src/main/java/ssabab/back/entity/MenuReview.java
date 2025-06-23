// dto.MenuReview
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * 메뉴 평균 평점, 후회 여부, 한 줄 평 엔티티 - 사용자가 남긴 메뉴의 평점의 평균, 후회 여부, 한 줄 평을 표현
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "menu_review")
@Data
@NoArgsConstructor
public class MenuReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long menuReviewId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "menu_score")
    private Double menuScore;

    @Column(name = "menu_regret")
    private Boolean menuRegret;

    @Column(name = "menu_comment")
    private String menuComment;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
