// entity.DmMonthlyClassEngagement
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable; // 복합 키를 위해 Serializable 임포트

// 복합 키를 위한 ID 클래스 정의 (내부에 또는 별도 파일로)
@Data
@NoArgsConstructor
@AllArgsConstructor
class DmMonthlyClassEngagementId implements Serializable {
    private static final long serialVersionUID = 1L;
    private String classNum; // class
    private String generation; // generation
    private Integer month; // month도 PK에 포함될 가능성 있음. (일반적으로 DM은 특정 월 기준)
    // 현재 스키마에는 month가 PK 명시가 없으나, 보통 월별 집계이므로 복합키에 포함하는 것이 일반적
    // 스키마에 PK 명시된 class, generation만 PK로 설정.
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "class_engagement_monthly")
@IdClass(DmMonthlyClassEngagementId.class) // 복합 기본 키 클래스 지정
public class DmMonthlyClassEngagement {

    @Id // 복합 키의 첫 번째 부분
    @Column(name = "class", columnDefinition = "TEXT") // SQL 예약어
    private String classNum;

    @Id // 복합 키의 두 번째 부분
    @Column(name = "generation", columnDefinition = "TEXT") // 기수
    private String generation;

    @Column(name = "\"month\"") // "month"는 SQL 예약어일 수 있으므로 쿼트 처리
    private Integer month;

    @Column(name = "review_count") // 해당 반의 월별 리뷰 수
    private Integer reviewCount;
}