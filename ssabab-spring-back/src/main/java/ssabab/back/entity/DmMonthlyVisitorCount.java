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
@Table(name = "dm_monthly_visitors")
public class DmMonthlyVisitorCount {
    // 스키마에 id 컬럼이 없으므로 제거합니다.
    // 하지만 JPA 엔티티는 PK를 가져야 하므로, 이 부분은 문제가 될 수 있습니다.
    // 만약 DB 스키마에 사실상 PK 역할을 하는 컬럼이 있다면 그에 맞춰 @Id를 붙여야 합니다.
    // 현재 스키마 정보만으로는 PK가 불분명합니다.
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;

    // 대안: 복합 키 (예: 월) 또는 암시적 PK (읽기 전용 뷰/테이블의 경우)
    // 현재 DM_MONTHLY_VISITORS는 MONTH 컬럼도 없고 USER_COUNT만 있으므로
    // 월별 방문자 수라면 'month' 컬럼이 있어야 하고, month가 PK가 될 수 있습니다.
    // 하지만 주어진 스키마에는 month가 없습니다.
    // JPA가 동작하려면 @Id가 필요하므로, 이 엔티티는 주어진 스키마만으로는 문제가 있습니다.
    // 임시로 id를 다시 추가하되, DB에 실제 id가 없으면 DDL 에러가 발생할 수 있습니다.
    // 이전에 DmMonthlyVisitorCount에 id가 있었다는 것은 DB에 id가 있을 것으로 추정하여 유지하는 것이 합리적입니다.
    @Id // JPA 요구사항
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에 id가 AUTO_INCREMENT PK여야 함
    private Long id;

    // 월간 방문자 수이므로 월 정보가 필수적으로 필요합니다.
    // ERD 이미지 (image 5)에 dm_monthly_visitors에 month 컬럼이 있었으므로 다시 추가합니다.
    @Column(name = "\"month\"")
    private Integer month;

    @Column(name = "user_count")
    private Integer userCount;
}