package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.FriendMenuReviewResponseDTO;
import ssabab.back.dto.MenuReviewRequestDTO;
import ssabab.back.entity.*;
import ssabab.back.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuReviewService {

    private final MenuReviewRepository menuReviewRepository;
    private final AccountRepository accountRepository;
    private final MenuRepository menuRepository;
    private final FriendRepository friendRepository;

    /**
     * 메뉴에 대한 후회 여부 및 한 줄 평 등록 또는 수정
     * → 로그인된 사용자 기준
     */
    @Transactional
    public void submitMenuReview(MenuReviewRequestDTO request) {
        Account user = getLoginUser();
        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new NoSuchElementException("메뉴가 존재하지 않습니다."));

        MenuReview menuReview = menuReviewRepository.findByUserUserIdAndMenuMenuId(user.getUserId(), menu.getMenuId())
                .orElseGet(() -> {
                    MenuReview newReview = new MenuReview();
                    newReview.setUser(user);
                    newReview.setMenu(menu);
                    return newReview;
                });
        if (request.getMenuScore() != null) {
            menuReview.setMenuScore(request.getMenuScore());
        }
        if (request.getMenuComment() != null) {
            menuReview.setMenuComment(request.getMenuComment());
        }

        if (request.getMenuRegret() != null) {
            menuReview.setMenuRegret(request.getMenuRegret());
        }

        menuReview.setTimestamp(LocalDateTime.now());

        menuReviewRepository.save(menuReview);
    }

    /**
     * 친구들이 남긴 메뉴 평점 평균을 반환
     * → 로그인된 사용자의 친구 기준
     */
    @Transactional(readOnly = true)
    public List<FriendMenuReviewResponseDTO> getFriendsMenuReviewStats(LocalDate date) {
        Account user = getLoginUser();
        Long userId = user.getUserId();
        List<Friend> friends = friendRepository.findByUserUserId(userId);

        return friends.stream()
                .map(Friend::getFriend)
                .map(friend -> menuReviewRepository.findByUserUserIdAndMenuDate(friend.getUserId(), date).stream()
                        .findFirst()
                        .map(menuReview -> {
                            Menu menu = menuReview.getMenu();
                            List<FriendMenuReviewResponseDTO.FoodInfo> foodInfos = menu.getFoods().stream()
                                    .map(food -> new FriendMenuReviewResponseDTO.FoodInfo(
                                            food.getFoodId(),
                                            food.getFoodName()
                                    ))
                                    .collect(Collectors.toList());

                            return FriendMenuReviewResponseDTO.builder()
                                    .friendId(friend.getUserId())
                                    .friendName(friend.getUsername())
                                    .votedMenuId(menu.getMenuId())
                                    .votedMenuDate(menu.getDate())
                                    .votedMenuInfo(foodInfos)
                                    .averageMenuScore(menuReview.getMenuScore())
                                    .build();
                        })
                        .orElse(null) // 해당 날짜에 리뷰가 없는 친구는 null 반환 후 필터링
                )
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * 현재 로그인한 사용자 Account 반환
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