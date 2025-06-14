package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Account;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByProviderAndProviderId(String provider, String providerId);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUsername(String username);
}
