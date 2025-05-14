package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import ssabab.back.entity.Account;

public interface AccountRepository extends JpaRepository<Account, INTGER> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByProviderAndProviderId(String provider, String providerId);
}
