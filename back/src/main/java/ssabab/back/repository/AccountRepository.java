package ssabab.back.repository;

import ssabab.back.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // 이메일로 회원 정보 조회 (select * from account where account_email=?)
    Optional<AccountEntity> findByAccountEmail(String accountEmail);
}
