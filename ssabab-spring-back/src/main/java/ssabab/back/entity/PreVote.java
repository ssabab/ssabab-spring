package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * 사전 투표 엔티티 - 사용자의 사전 투표 결과를 기록
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "pre_vote")
@Data
@NoArgsConstructor
public class PreVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_vote_id")
    private Long preVoteId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;
}
