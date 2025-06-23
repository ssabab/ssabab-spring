// repository.PreVoteRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.PreVote;

import java.time.LocalDate;
import java.util.Optional;

public interface PreVoteRepository extends JpaRepository<PreVote, Long> {

    // 특정 사용자가 특정 날짜의 메뉴 중 하나에 이미 투표했는지 확인
    Optional<PreVote> findByUserUserIdAndMenuDate(Long userId, LocalDate date);

    // 특정 메뉴에 특정 사용자가 투표했는지 확인 (조회 용도)
    Optional<PreVote> findByUserUserIdAndMenuMenuId(Long userId, Long menuId);
}
