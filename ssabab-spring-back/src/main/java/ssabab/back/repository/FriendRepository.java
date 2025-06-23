// repository.FriendRepository
package ssabab.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssabab.back.entity.Friend;
import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByUserUserIdAndFriendUserId(Long userId, Long friendId);
    void deleteByUserUserIdAndFriendUserId(Long userId, Long friendId);
    List<Friend> findByUserUserId(Long userId);
}
