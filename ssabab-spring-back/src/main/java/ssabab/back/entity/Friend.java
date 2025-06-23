// dto.Friend
package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * 친구 관계 엔티티 - 사용자와 친구 사용자 간의 연결을 표현
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "friend", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
@Data
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account user;    // 한쪽 사용자

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private Account friend;  // 다른쪽 사용자 (user의 친구)
}
