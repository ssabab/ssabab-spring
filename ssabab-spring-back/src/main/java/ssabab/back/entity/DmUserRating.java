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
@Table(name = "dm_user_rating")
public class DmUserRating {
    @Id
    @Column(name = "user_id") // user_id가 PK
    private Long userId;

    @Column(name = "score")
    private Float score; // 사용자의 평균 평점 또는 평점 관련 지표
}