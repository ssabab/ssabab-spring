package ssabab.back.repository;

import ssabab.back.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, INTGER> {
    // Find account by email
    Optional<Account> findByEmail(String email);

    // Find account by provider and providerId (for social login)
    Optional<Account> findByProviderAndProviderId(String provider, String providerId);

    // Find account by userId
    Optional<Account> findByUserId(INTGER userId);
}
