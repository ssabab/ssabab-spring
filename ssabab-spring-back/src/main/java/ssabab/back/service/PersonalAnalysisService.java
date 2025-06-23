// service/PersonalAnalysisService.java
package ssabab.back.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.*;
import ssabab.back.entity.*;
import ssabab.back.repository.*;
import ssabab.back.enums.GroupType;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PersonalAnalysisService {

    private final AccountRepository accountRepository;
    private final DmUserSummaryRepository dmUserSummaryRepository;
    private final DmUserFoodRatingRankRepository dmUserFoodRatingRankRepository;
    private final DmUserCategoryStatsRepository dmUserCategoryStatsRepository;
    private final DmUserReviewWordRepository dmUserReviewWordRepository;
    private final DmUserInsightRepository dmUserInsightRepository;
    private final DmUserDiversityComparisonRepository dmUserDiversityComparisonRepository;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();


    @Transactional(readOnly = true)
    public PersonalAnalysisResponse getPersonalAnalysisData() throws JsonProcessingException {
        Account user = getLoginUser();
        Long userId = user.getUserId();

        // Rating Data
        DmUserSummary summary = dmUserSummaryRepository.findById(userId)
                .orElse(new DmUserSummary(userId, 0.0f, 0, 0));
        RatingDataDTO ratingData = new RatingDataDTO(
                summary.getAvgScore() != null ? summary.getAvgScore().doubleValue() : 0.0,
                summary.getTotalReviews()
        );

        // Top/Lowest Rated Foods
        List<DmUserFoodRatingRank> ranks = dmUserFoodRatingRankRepository.findByUserId(userId);
        List<TopLowestRatedFoodsDTO> topRatedFoods = ranks.stream()
                .filter(r -> "best".equals(r.getScoreType().name()))
                .sorted(Comparator.comparing(DmUserFoodRatingRank::getRankOrder))
                .map(r -> new TopLowestRatedFoodsDTO(r.getFoodName(), r.getFoodScore().doubleValue(), "N/A")) // date is not available
                .collect(Collectors.toList());

        List<TopLowestRatedFoodsDTO> lowestRatedFoods = ranks.stream()
                .filter(r -> "worst".equals(r.getScoreType().name()))
                .sorted(Comparator.comparing(DmUserFoodRatingRank::getRankOrder))
                .map(r -> new TopLowestRatedFoodsDTO(r.getFoodName(), r.getFoodScore().doubleValue(), "N/A")) // date is not available
                .collect(Collectors.toList());

        // Preferred Categories
        List<DmUserCategoryStats> categoryStats = dmUserCategoryStatsRepository.findByUserId(userId);
        long totalCategoryCount = categoryStats.stream().mapToLong(DmUserCategoryStats::getCount).sum();
        List<PreferredCategoryDTO> preferredCategories = categoryStats.stream()
                .map(s -> new PreferredCategoryDTO(
                        s.getCategory(),
                        totalCategoryCount > 0 ? (int) Math.round(((double) s.getCount() / totalCategoryCount) * 100) : 0
                ))
                .sorted(Comparator.comparing(PreferredCategoryDTO::getPercentage).reversed())
                .collect(Collectors.toList());

        // Preferred Keywords for Word Cloud
        List<DmUserReviewWord> words = dmUserReviewWordRepository.findByUserId(userId);
        List<PreferredKeywordDTO> preferredKeywordsForCloud = words.stream()
                .map(w -> new PreferredKeywordDTO(
                        w.getWord(),
                        w.getCount(),
                        generateRandomHexColor()
                ))
                .collect(Collectors.toList());

        // Personal Insight
        String personalInsight = dmUserInsightRepository.findById(userId)
                .map(DmUserInsight::getInsight)
                .orElse("아직 분석된 인사이트가 없습니다.");

        // Comparison Data (assuming 'all' group type for community average)
        DmUserDiversityComparison comparison = dmUserDiversityComparisonRepository.findByUserIdAndGroupType(userId, GroupType.all)
                .orElse(new DmUserDiversityComparison());

        ComparisonDataDTO comparisonData = ComparisonDataDTO.builder()
                .myRating(comparison.getUserAvgScore() != null ? comparison.getUserAvgScore().doubleValue() : 0.0)
                .avgRatingCommunity(comparison.getGroupAvgScore() != null ? comparison.getGroupAvgScore().doubleValue() : 0.0)
                // Note: spicy/variety seeking mapping is an interpretation of diversity score
                .mySpicyPreference(comparison.getUserDiversityScore() != null ? comparison.getUserDiversityScore().doubleValue() * 5 : 2.5) // Example mapping
                .avgSpicyCommunity(comparison.getGroupDiversityScore() != null ? comparison.getGroupDiversityScore().doubleValue() * 5 : 2.5) // Example mapping
                .myVarietySeeking(comparison.getUserDiversityScore() != null ? (1 - comparison.getUserDiversityScore().doubleValue()) * 5 : 2.5) // Example mapping
                .avgVarietyCommunity(comparison.getGroupDiversityScore() != null ? (1 - comparison.getGroupDiversityScore().doubleValue()) * 5 : 2.5) // Example mapping
                .build();


        return PersonalAnalysisResponse.builder()
                .ratingData(ratingData)
                .topRatedFoods(topRatedFoods)
                .lowestRatedFoods(lowestRatedFoods)
                .preferredCategories(preferredCategories)
                .preferredKeywordsForCloud(preferredKeywordsForCloud)
                .personalInsight(personalInsight)
                .comparisonData(comparisonData)
                .build();
    }

    private String generateRandomHexColor() {
        // Generate random R, G, B values
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02x%02x%02x", r, g, b).toUpperCase();
    }

    private Account getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자 정보가 없습니다."));
    }
}