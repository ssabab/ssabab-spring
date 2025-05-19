package ssabab.back.repository;

import ssabab.back.entity.Account;
import ssabab.back.entity.Menu;
import ssabab.back.entity.PreVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreVoteRepository extends JpaRepository<PreVote, Integer> {
    List<PreVote> findByMenu(Menu menu);
    List<PreVote> findByAccount(Account account);
    Optional<PreVote> findByMenuAndAccount(Menu menu, Account account);

    @Query("SELECT COUNT(p) FROM PreVote p WHERE p.menu = :menu")
    Integer countVotesByMenu(@Param("menu") Menu menu);
}