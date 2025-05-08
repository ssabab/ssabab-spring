package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
