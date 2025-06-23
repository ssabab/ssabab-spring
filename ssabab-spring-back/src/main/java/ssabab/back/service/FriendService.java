// service.FriendService
package ssabab.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.AccountDTO;
import ssabab.back.entity.Account;
import ssabab.back.entity.Friend;
import ssabab.back.repository.AccountRepository;
import ssabab.back.repository.FriendRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 친구 관리 비즈니스 로직 서비스
 */
@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * 현재 로그인한 사용자 Account 반환 (유틸리티 메서드)
     */
    private Account getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        String email = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // UserDetails의 username은 여기서는 email
        } else {
            throw new IllegalStateException("인증 정보에서 사용자 이메일을 찾을 수 없습니다.");
        }

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("로그인된 사용자 정보를 찾을 수 없습니다."));
    }

    /**
     * 사용자의 친구 목록을 AccountDTO 리스트로 반환
     */
    public List<AccountDTO> getFriendList(Long userId) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));
        List<Friend> friends = friendRepository.findByUserUserId(userId);
        // Friend 목록의 friend(Account) 객체를 AccountDTO로 매핑
        return friends.stream().map(relation -> {
            Account friendAccount = relation.getFriend();
            AccountDTO dto = new AccountDTO();
            dto.setUserId(friendAccount.getUserId());
            dto.setUsername(friendAccount.getUsername());
            dto.setEmail(friendAccount.getEmail());
            dto.setSsafyYear(friendAccount.getSsafyYear());
            dto.setClassNum(friendAccount.getClassNum());
            dto.setSsafyRegion(friendAccount.getSsafyRegion());
            dto.setGender(friendAccount.getGender());
            dto.setBirthDate(friendAccount.getBirthDate());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 친구 추가 - 현재 사용자와 대상 사용자 간의 친구 관계 2건 생성
     */
    @Transactional
    public void addFriend(Account user, String friendUsername) {
        if (user.getUsername().equals(friendUsername)) {
            throw new IllegalArgumentException("자기 자신은 친구로 추가할 수 없습니다.");
        }
        // 친구로 추가할 사용자 계정 조회 (닉네임으로 검색)
        Account friendAccount = accountRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new NoSuchElementException("해당 닉네임의 사용자를 찾을 수 없습니다."));
        Long userId = user.getUserId();
        Long friendId = friendAccount.getUserId();
        // 이미 친구인지 확인
        if (friendRepository.existsByUserUserIdAndFriendUserId(userId, friendId)) {
            throw new IllegalStateException("이미 친구로 추가된 사용자입니다.");
        }
        // 양방향 친구 관계 엔티티 생성
        Friend relation1 = new Friend();
        relation1.setUser(user);
        relation1.setFriend(friendAccount);
        Friend relation2 = new Friend();
        relation2.setUser(friendAccount);
        relation2.setFriend(user);
        // 관계 저장
        friendRepository.save(relation1);
        friendRepository.save(relation2);
    }

    /**
     * 친구 삭제 - 양방향 관계 모두 제거
     */
    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        // 친구 관계 존재 여부 확인
        if (!friendRepository.existsByUserUserIdAndFriendUserId(userId, friendId)) {
            throw new NoSuchElementException("친구 관계가 존재하지 않습니다.");
        }
        // 양쪽 Friend 엔티티 삭제
        friendRepository.deleteByUserUserIdAndFriendUserId(userId, friendId);
        friendRepository.deleteByUserUserIdAndFriendUserId(friendId, userId);
    }
}