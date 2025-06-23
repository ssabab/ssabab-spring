// controller.FriendController
package ssabab.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.AccountDTO;
import ssabab.back.entity.Account;
import ssabab.back.repository.AccountRepository;
import ssabab.back.service.FriendService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * 친구 기능 관련 API 컨트롤러 - 친구 목록 조회, 추가, 삭제
 */
@RestController
@RequestMapping("/friends")
public class FriendController {
    @Autowired
    private FriendService friendService;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * 현재 로그인된 사용자의 Account 객체를 가져오는 헬퍼 메소드
     */
    private Account getLoggedInAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        String email = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            throw new IllegalStateException("인증 정보에서 사용자 이메일을 찾을 수 없습니다.");
        }

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("로그인된 사용자 정보를 찾을 수 없습니다."));
    }

    /**
     * 친구 목록 조회 (현재 사용자)
     */
    @GetMapping
    public ResponseEntity<Object> getFriends() {
        try {
            Account currentUser = getLoggedInAccount();
            List<AccountDTO> friends = friendService.getFriendList(currentUser.getUserId());
            return ResponseEntity.ok(Map.of("friends", friends));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "친구 목록 조회 중 오류 발생: " + e.getMessage()));
        }
    }

    /**
     * 친구 추가
     */
    @PostMapping
    public ResponseEntity<Object> addFriend(@RequestBody Map<String, String> req) {
        try {
            Account currentUser = getLoggedInAccount();
            String friendUsername = req.get("username");
            if (friendUsername == null || friendUsername.isBlank()) {
                friendUsername = req.get("name"); // 혹시 name으로 올 경우 대비
                if (friendUsername == null || friendUsername.isBlank()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Friend username is required"));
                }
            }
            friendService.addFriend(currentUser, friendUsername);
            return ResponseEntity.ok(Map.of("message", "Friend added", "friend", friendUsername));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "친구 추가 중 오류 발생: " + e.getMessage()));
        }
    }

    /**
     * 친구 삭제
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Object> deleteFriend(@PathVariable Long friendId) {
        try {
            Account currentUser = getLoggedInAccount();
            friendService.removeFriend(currentUser.getUserId(), friendId);
            return ResponseEntity.ok(Map.of("message", "Friend deleted", "friendId", friendId));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "친구 삭제 중 오류 발생: " + e.getMessage()));
        }
    }
}