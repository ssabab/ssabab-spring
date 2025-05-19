package ssabab.back.repository;

import ssabab.back.entity.Account;
import ssabab.back.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    List<Friend> findByUser(Account user);
    List<Friend> findByFriend(Account friend);
}