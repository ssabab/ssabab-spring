// service.PreVoteService.java
package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.FriendPreVoteResponseDTO;
import ssabab.back.dto.PreVoteRequestDTO;
import ssabab.back.dto.PreVoteResponseDTO; // --- [추가된 import] ---
import ssabab.back.entity.*;
import ssabab.back.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional; // --- [추가된 import] ---
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreVoteService {

    private final PreVoteRepository preVoteRepository;
    private final AccountRepository accountRepository;
    private final MenuRepository menuRepository;
    private final FriendRepository friendRepository;

    /**
     * 사전 투표 등록 또는 수정 (날짜별로 1회만 가능)
     */
    @Transactional
    public void submitPreVote(PreVoteRequestDTO request) {
        Account user = getLoginUser();
        Menu selectedMenu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new NoSuchElementException("메뉴가 존재하지 않습니다."));

        LocalDate date = selectedMenu.getDate();

        // 같은 날짜에 이미 투표한 기록이 있다면 수정, 없다면 새로 등록
        preVoteRepository.findByUserUserIdAndMenuDate(user.getUserId(), date)
                .ifPresentOrElse(
                        existingVote -> {
                            existingVote.setMenu(selectedMenu);
                            preVoteRepository.save(existingVote);
                        },
                        () -> {
                            PreVote newVote = new PreVote();
                            newVote.setUser(user);
                            newVote.setMenu(selectedMenu);
                            preVoteRepository.save(newVote);
                        }
                );
    }


    /**
     * 특정 날짜에 대한 현재 사용자의 사전 투표 정보를 조회합니다.
     * @param date 조회할 날짜
     * @return 사용자가 투표한 메뉴 ID가 담긴 DTO. 투표하지 않았다면 menuId는 null.
     */
    @Transactional(readOnly = true)
    public PreVoteResponseDTO getUserPreVoteForDate(LocalDate date) {
        Account user = getLoginUser();

        // findByUserUserIdAndMenuDate를 사용하여 해당 날짜의 투표를 찾습니다.
        Optional<PreVote> preVoteOpt = preVoteRepository.findByUserUserIdAndMenuDate(user.getUserId(), date);

        // Optional을 처리하여 menuId를 추출하거나, 없으면 null을 반환합니다.
        Long votedMenuId = preVoteOpt.map(preVote -> preVote.getMenu().getMenuId()).orElse(null);

        return new PreVoteResponseDTO(votedMenuId);
    }


    /**
     * 친구들의 사전 투표 결과 조회 (로그인 사용자 기준)
     */
    @Transactional(readOnly = true)
    public List<FriendPreVoteResponseDTO> getFriendsPreVotes(LocalDate date) {
        Account user = getLoginUser();
        Long userId = user.getUserId();
        List<Friend> friends = friendRepository.findByUserUserId(userId);

        return friends.stream()
                .map(Friend::getFriend)
                .map(friend -> preVoteRepository.findByUserUserIdAndMenuDate(friend.getUserId(), date)
                        .map(preVote -> {
                            List<FriendPreVoteResponseDTO.FoodInfo> foodInfos = preVote.getMenu().getFoods().stream()
                                    .map(food -> new FriendPreVoteResponseDTO.FoodInfo(
                                            food.getFoodId(),
                                            food.getFoodName()
                                    ))
                                    .collect(Collectors.toList());

                            return FriendPreVoteResponseDTO.builder()
                                    .friendId(friend.getUserId())
                                    .friendName(friend.getUsername())
                                    .votedMenuId(preVote.getMenu().getMenuId())
                                    .votedMenuDate(preVote.getMenu().getDate())
                                    .votedMenuInfo(foodInfos)
                                    .build();
                        })
                        .orElse(null) // 해당 날짜에 리뷰가 없는 친구는 null 반환 후 필터링
                )
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * 현재 로그인한 사용자 조회
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
                .orElseThrow(() -> new NoSuchElementException("사용자 정보가 없습니다."));
    }
}