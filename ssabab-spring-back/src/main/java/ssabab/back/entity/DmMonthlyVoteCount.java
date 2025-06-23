// dto.DmMonthlyVoteCount
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable; // 복합 키를 위해 Serializable 임포트

// DmMonthlyVoteCount 복합 키를 위한 ID 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
class DmMonthlyVoteCountId implements Serializable {
    private static final long serialVersionUID = 1L;
    private String classNum; // class
    private String generation; // generation
    private String gender; // gender
    private Integer age; // age
    private Integer month; // month도 복합키에 포함될 가능성 있음 (일반적인 월간 집계)
    // 현재 스키마에 month 명시가 없으나, 필요시 추가
    // 일단 스키마에 명시된 4개만 PK로.
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dm_monthly_vote_count")
@IdClass(DmMonthlyVoteCountId.class) // 복합 기본 키 클래스 지정
public class DmMonthlyVoteCount {

    @Id // 복합 키의 첫 번째 부분
    @Column(name = "class", length = 255) // 스키마 varchar(255) PK
    private String classNum; // 엔티티 필드명 충돌 방지

    @Id // 복합 키의 두 번째 부분
    @Column(name = "generation", length = 255) // 스키마 varchar(255) PK
    private String generation;

    @Id // 복합 키의 세 번째 부분
    @Column(name = "gender", length = 255) // 스키마 varchar(255) PK
    private String gender;

    @Id // 복합 키의 네 번째 부분
    @Column(name = "age") // 스키마 int PK
    private Integer age;

    @Column(name = "review_count") // 스키마 int
    private Integer reviewCount;

    // 월 컬럼 추가 (ERD에 별도 명시 없지만, 월간 대시보드라면 필요)
    // 이전에 DmMonthlyVoteCount에 month 컬럼이 있었고, 월간 분석에 필수적이므로 추가.
    @Column(name = "\"month\"") // "month"는 SQL 예약어일 수 있으므로 쿼트 처리
    private Integer month;
}