// entity.DmMonthlyVisitorCount
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

    @Id
    @Column(name = "user_count")
    private Integer userCount;
    // 월간 방문자 수이므로 월 정보가 필수적으로 필요합니다.
    // ERD 이미지 (image 5)에 dm_monthly_visitors에 month 컬럼이 있었으므로 다시 추가합니다
    @Column(name = "\"month\"")
    private Integer month;

}