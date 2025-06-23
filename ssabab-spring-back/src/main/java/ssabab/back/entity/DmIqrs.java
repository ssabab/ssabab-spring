// entity.DmIqrs
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssabab.back.enums.IqrType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dm_iqrs")
public class DmIqrs {
    @Id // iqr_type이 PK이므로 id는 PK가 아님
    @Enumerated(EnumType.STRING)
    @Column(name = "iqr_type", length = 20) // iqr_type이 PK
    private IqrType iqrType;

    // id는 PK가 아니므로 @Id, @GeneratedValue 제거. DB 스키마에 id 컬럼이 없다면 이 필드는 제거합니다.
    // 현재 스키마에는 id가 없습니다.
    // private Long id;

    @Column(name = "iqr_value") // 스키마에 iqr_value float로 존재
    private Float iqrValue;

    @Column(name = "q1")
    private Float q1; // 1사분위수
    @Column(name = "q2")
    private Float q2; // 2사분위수 (중앙값)
    @Column(name = "q3")
    private Float q3; // 3사분위수
}