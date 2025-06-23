// dto.DmUserRatingTopBottom
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dm_user_rating_top_bottom")
public class DmUserRatingTopBottom {
    @Id
    @Column(name = "user_id") // user_id가 PK
    private Long userId;

    // Best Foods
    @Column(name = "1_best_food1_name", columnDefinition = "LONGTEXT") // longtext 타입
    private String bestFood1Name;
    @Column(name = "1_best_food1_score") // double 타입
    private Double bestFood1Score; // Float 대신 Double 사용

    @Column(name = "2_best_food2_name", columnDefinition = "LONGTEXT")
    private String bestFood2Name;
    @Column(name = "2_best_food2_score")
    private Double bestFood2Score;

    @Column(name = "3_best_food3_name", columnDefinition = "LONGTEXT")
    private String bestFood3Name;
    @Column(name = "3_best_food3_score")
    private Double bestFood3Score;

    @Column(name = "4_best_food4_name", columnDefinition = "LONGTEXT")
    private String bestFood4Name;
    @Column(name = "4_best_food4_score")
    private Double bestFood4Score;

    @Column(name = "5_best_food5_name", columnDefinition = "LONGTEXT")
    private String bestFood5Name;
    @Column(name = "5_best_food5_score")
    private Double bestFood5Score;

    // Worst Foods
    @Column(name = "1_worst_food1_name", columnDefinition = "LONGTEXT")
    private String worstFood1Name;
    @Column(name = "1_worst_food1_score")
    private Double worstFood1Score;

    @Column(name = "2_worst_food2_name", columnDefinition = "LONGTEXT")
    private String worstFood2Name;
    @Column(name = "2_worst_food2_score")
    private Double worstFood2Score;

    @Column(name = "3_worst_food3_name", columnDefinition = "LONGTEXT")
    private String worstFood3Name;
    @Column(name = "3_worst_food3_score")
    private Double worstFood3Score;

    @Column(name = "4_worst_food4_name", columnDefinition = "LONGTEXT")
    private String worstFood4Name;
    @Column(name = "4_worst_food4_score")
    private Double worstFood4Score;

    @Column(name = "5_worst_food5_name", columnDefinition = "LONGTEXT")
    private String worstFood5Name;
    @Column(name = "5_worst_food5_score")
    private Double worstFood5Score;
}