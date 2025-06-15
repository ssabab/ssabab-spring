// ssabab/back/repository/DmMonthlyVoteCountRepository.java
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmMonthlyVoteCount;

import java.util.List;

public interface DmMonthlyVoteCountRepository extends JpaRepository<DmMonthlyVoteCount, Long> {
    List<DmMonthlyVoteCount> findByMonth(Integer month);
}