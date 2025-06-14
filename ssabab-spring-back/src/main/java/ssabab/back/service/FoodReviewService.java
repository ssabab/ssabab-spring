package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.FoodReviewRequestDTO;
import ssabab.back.entity.*;
import ssabab.back.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class FoodReviewService {

    private final FoodReviewRepository foodReviewRepository;
    private final MenuReviewRepository menuReviewRepository;
    private final AccountRepository accountRepository;
    private final FoodRepository foodRepository;
    private final MenuRepository menuRepository;

    /**
     * 여러 음식 평점을 저장하고 해당 메뉴의 평균 평점을 menu_review에 반영
     * 로그인된 사용자 기준
     */
    @Transactional
    public void submitFoodReviews(Long menuId, List<FoodReviewRequestDTO.ReviewItem> foodReviews) {
        Account user = getLoginUser();
        Long userId = user.getUserId();

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("메뉴가 존재하지 않습니다."));

        for (FoodReviewRequestDTO.ReviewItem dto : foodReviews) {
            Food food = foodRepository.findById(dto.getFoodId())
                    .orElseThrow(() -> new NoSuchElementException("음식을 찾을 수 없습니다: " + dto.getFoodId()));

            foodReviewRepository.findByUserUserIdAndFoodFoodId(userId, dto.getFoodId())
                    .ifPresentOrElse(
                            existing -> {
                                existing.setFoodScore(dto.getFoodScore());
                                existing.setTimestamp(LocalDateTime.now());
                                foodReviewRepository.save(existing);
                            },
                            () -> {
                                FoodReview newReview = new FoodReview();
                                newReview.setUser(user);
                                newReview.setFood(food);
                                newReview.setFoodScore(dto.getFoodScore());
                                newReview.setTimestamp(LocalDateTime.now());
                                foodReviewRepository.save(newReview);
                            }
                    );
        }

        // 해당 메뉴에 대한 사용자의 모든 음식 리뷰를 가져와 평균 계산
        List<FoodReview> userFoodReviewsForMenu = foodReviewRepository.findAll().stream()
                .filter(fr -> fr.getUser().getUserId().equals(userId) &&
                        menu.getFoods().contains(fr.getFood()))
                .toList();


        OptionalDouble averageScore = userFoodReviewsForMenu.stream()
                .mapToLong(FoodReview::getFoodScore)
                .average();

        if (averageScore.isPresent()) {
            MenuReview menuReview = menuReviewRepository.findByUserUserIdAndMenuMenuId(userId, menuId)
                    .orElseGet(() -> {
                        MenuReview newReview = new MenuReview();
                        newReview.setUser(user);
                        newReview.setMenu(menu);
                        return newReview;
                    });

            menuReview.setMenuScore(averageScore.getAsDouble());
            menuReview.setTimestamp(LocalDateTime.now());
            menuReviewRepository.save(menuReview);
        }
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
                .orElseThrow(() -> new NoSuchElementException("로그인된 사용자 정보를 찾을 수 없습니다."));
    }
}