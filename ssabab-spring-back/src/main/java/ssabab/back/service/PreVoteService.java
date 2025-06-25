package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.FriendPreVoteResponseDTO;
import ssabab.back.dto.PreVoteRequestDTO;
import ssabab.back.entity.*;
import ssabab.back.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreVoteService {

    private final PreVoteRepository preVoteRepository;
    private final AccountRepository accountRepository;
    private final MenuRepository menuRepository;
    private final FriendRepository friendRepository;

    // 사전 투표 등록
    @Transactional
    public void registerPreVote(PreVoteRequestDTO request) {
        Account user = getLoginUser();
        Menu selectedMenu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new NoSuchElementException("메뉴가 존재하지 않습니다."));

        LocalDate date = selectedMenu.getDate();

        boolean alreadyVoted = preVoteRepository.findByUserUserIdAndMenuDate(user.getUserId(), date).isPresent();
        if (alreadyVoted) {
            throw new IllegalStateException("이미 해당 날짜에 투표가 존재합니다");
        }

        PreVote newVote = new PreVote();
        newVote.setUser(user);
        newVote.setMenu(selectedMenu);
        preVoteRepository.save(newVote);
    }

    // 사전 투표 수정
    @Transactional
    public void updatePreVote(PreVoteRequestDTO request) {
        Account user = getLoginUser();
        Menu selectedMenu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new NoSuchElementException("메뉴가 존재하지 않습니다."));

        LocalDate date = selectedMenu.getDate();

        PreVote existingVote = preVoteRepository.findByUserUserIdAndMenuDate(user.getUserId(), date)
                .orElseThrow(() -> new NoSuchElementException("기존 투표 내역이 없습니다"));

        existingVote.setMenu(selectedMenu);
        preVoteRepository.save(existingVote);
    }

    // 친구 투표 결과 조회
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
                        .orElse(null)
                )
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    // 현재 로그인한 사용자 조회
    private Account getLoginUser() {
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
                .orElseThrow(() -> new NoSuchElementException("사용자 정보가 없습니다."));
    }
}
