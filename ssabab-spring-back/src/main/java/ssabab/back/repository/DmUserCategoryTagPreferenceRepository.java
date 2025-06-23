// repository.DmUserCategoryTagPreferenceRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.DmUserCategoryTagPreference;

import java.util.Optional;

public interface DmUserCategoryTagPreferenceRepository extends JpaRepository<DmUserCategoryTagPreference, Long> {
    Optional<DmUserCategoryTagPreference> findByUserId(Long userId);
}