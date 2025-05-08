package ssabab.back.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.dto.FoodReviewDTO;
import ssabab.back.dto.FoodReviewRequestDTO;
import ssabab.back.entity.*;
import ssabab.back.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final AccountRepository accountRepository;
    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final FoodReviewRepository foodReviewRepository;

    @Transactional
    public FoodReviewRequestDTO getFoodReviews(Integer userId, Integer menuId) {
        Account user = accountRepository.findById(userId).orElseThrow();
        Menu menu = menuRepository.findById(menuId).orElseThrow();

        List<FoodReview> reviews = foodReviewRepository.findByAccountAndFoodIn(user, menu.getFoods());

        return FoodReviewRequestDTO.builder()
                .foods(menu.getFoods().stream()
                        .map(food -> {
                            Integer score = reviews.stream()
                                    .filter(r -> r.getFood().getFoodId().equals(food.getFoodId()))
                                    .map(FoodReview::getFoodScore)
                                    .findFirst().orElse(0);
                            return FoodReviewDTO.builder()
                                    .foodId(food.getFoodId())
                                    .foodName(food.getFoodName())
                                    .foodScore(score)
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void createOrUpdateFoodReviews(Integer userId, Integer menuId, FoodReviewRequestDTO dto, boolean isUpdate) {
        Account user = accountRepository.findById(userId).orElseThrow();
        Menu menu = menuRepository.findById(menuId).orElseThrow();

        List<Food> foods = menu.getFoods();
        if (isUpdate) {
            foodReviewRepository.deleteByAccountAndFoodIn(user, foods);
        }

        for (FoodReviewDTO f : dto.getFoods()) {
            Food food = foodRepository.findById(f.getFoodId()).orElseThrow();
            foodReviewRepository.save(FoodReview.builder()
                    .account(user)
                    .food(food)
                    .foodScore(f.getFoodScore())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    @Transactional
    public void deleteFoodReviews(Integer userId, Integer menuId) {
        Account user = accountRepository.findById(userId).orElseThrow();
        Menu menu = menuRepository.findById(menuId).orElseThrow();
        foodReviewRepository.deleteByAccountAndFoodIn(user, menu.getFoods());
    }
}