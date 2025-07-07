// service.FoodReviewService
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
import java.util.Map; // Map import 추가
import java.util.stream.Collectors; // Collectors import 추가

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
    @Transactional // 트랜잭션 안에서 모든 변경사항이 커밋됩니다.
    public void submitFoodReviews(Long menuId, List<FoodReviewRequestDTO.ReviewItem> foodReviews) {
        Account user = getLoginUser();
        Long userId = user.getUserId();

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("메뉴가 존재하지 않습니다."));

        // 1. 요청에 포함된 모든 foodId를 미리 추출
        List<Long> requestFoodIds = foodReviews.stream()
                .map(FoodReviewRequestDTO.ReviewItem::getFoodId)
                .collect(Collectors.toList());

        // 2. 요청된 모든 Food 엔티티를 한 번의 쿼리로 조회 (findAllById 사용)
        // 이 경우 FoodRepository에 findAllById(Iterable<ID>) 메서드가 기본적으로 존재합니다.
        Map<Long, Food> foodsMap = foodRepository.findAllById(requestFoodIds).stream()
                .collect(Collectors.toMap(Food::getFoodId, food -> food));

        // 3. 해당 사용자가 요청된 음식들에 대해 기존에 작성한 모든 FoodReview를 한 번의 쿼리로 조회
        Map<Long, FoodReview> existingReviewMap = foodReviewRepository.findByUserUserIdAndFoodFoodIdIn(userId, requestFoodIds).stream()
                .collect(Collectors.toMap(fr -> fr.getFood().getFoodId(), fr -> fr));

        List<FoodReview> reviewsToSave = new java.util.ArrayList<>();

        // 4. 각 리뷰 아이템을 순회하며 새로운 Review를 생성하거나 기존 Review를 업데이트
        for (FoodReviewRequestDTO.ReviewItem dto : foodReviews) {
            Food food = foodsMap.get(dto.getFoodId());
            if (food == null) {
                // 요청된 foodId에 해당하는 Food가 없는 경우 예외 처리
                throw new NoSuchElementException("음식을 찾을 수 없습니다: " + dto.getFoodId());
            }

            FoodReview foodReview = existingReviewMap.get(dto.getFoodId());

            if (foodReview == null) {
                // 새로운 리뷰인 경우
                foodReview = new FoodReview();
                foodReview.setUser(user);
                foodReview.setFood(food);
            }
            // 기존 리뷰이거나 새로 생성된 리뷰의 점수와 타임스탬프 설정
            foodReview.setFoodScore(dto.getFoodScore());
            foodReview.setTimestamp(LocalDateTime.now());
            reviewsToSave.add(foodReview); // 저장할 리스트에 추가
        }

        // 5. 모든 변경 사항을 한 번에 배치로 저장/업데이트
        // JpaRepository의 saveAll 메서드는 내부적으로 JDBC 배치 기능을 활용합니다.
        foodReviewRepository.saveAll(reviewsToSave);

        // 6. 해당 메뉴에 대한 사용자의 모든 음식 리뷰를 가져와 평균 계산
        // 이 부분은 이미 개선된 findByUserUserIdAndFoodFoodIdIn을 사용하고 있으므로 유지
        List<Long> foodIdsInMenu = menu.getFoods().stream()
                .map(Food::getFoodId)
                .collect(Collectors.toList());
        List<FoodReview> userFoodReviewsForMenu = foodReviewRepository.findByUserUserIdAndFoodFoodIdIn(userId, foodIdsInMenu);


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
            email = userDetails.getUsername();
        } else {
            throw new IllegalStateException("인증 정보에서 사용자 이메일을 찾을 수 없습니다.");
        }

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("로그인된 사용자 정보를 찾을 수 없습니다."));
    }
}