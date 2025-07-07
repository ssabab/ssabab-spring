// service/PersonalAnalysisService.java
package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.dto.*;
import ssabab.back.entity.*;
import ssabab.back.enums.GroupType;
import ssabab.back.enums.ScoreType;
import ssabab.back.repository.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalAnalysisService {

    private final DmUserSummaryRepository dmUserSummaryRepository;
    private final DmUserFoodRatingRankRepository dmUserFoodRatingRankRepository;
    private final DmUserCategoryStatsRepository dmUserCategoryStatsRepository;
    private final DmUserTagStatsRepository dmUserTagStatsRepository;
    private final DmUserReviewWordRepository dmUserReviewWordRepository;
    private final DmUserInsightRepository dmUserInsightRepository;
    private final DmUserDiversityComparisonRepository dmUserDiversityComparisonRepository;

    public PersonalAnalysisResponse getPersonalAnalysis(Long userId) {
        // 1. dm_user_summary 조회 (명시적 쿼리 사용)
        RatingDataDTO summaryDto = dmUserSummaryRepository.findByUserId(userId)
                .map(summary -> new RatingDataDTO(summary.getUserId(), summary.getAvgScore(), summary.getTotalReviews(), summary.getPreVoteCount()))
                .orElseThrow(() -> new NoSuchElementException("사용자 요약 정보를 찾을 수 없습니다. (userId: " + userId + ")"));

        // 2. dm_user_food_rating_rank 조회 (명시적 쿼리 사용)
        List<DmUserFoodRatingRank> allRanks = dmUserFoodRatingRankRepository.findByUserId(userId);

        List<FoodRatingRankDTO> bestRanksDto = allRanks.stream()
                .filter(rank -> rank.getScoreType() == ScoreType.best)
                .map(rank -> new FoodRatingRankDTO(rank.getUserId(), rank.getFoodName(), rank.getFoodScore(), rank.getRankOrder(), rank.getScoreType()))
                .collect(Collectors.toList());

        List<FoodRatingRankDTO> worstRanksDto = allRanks.stream()
                .filter(rank -> rank.getScoreType() == ScoreType.worst)
                .map(rank -> new FoodRatingRankDTO(rank.getUserId(), rank.getFoodName(), rank.getFoodScore(), rank.getRankOrder(), rank.getScoreType()))
                .collect(Collectors.toList());

        // 3. dm_user_category_stats 조회 (명시적 쿼리 사용)
        List<CategoryStatsDTO> categoryStatsDto = dmUserCategoryStatsRepository.findByUserId(userId).stream()
                .map(stats -> new CategoryStatsDTO(stats.getUserId(), stats.getCategory(), stats.getCount()))
                .collect(Collectors.toList());

        // 4. dm_user_tag_stats 조회 (명시적 쿼리 사용)
        List<TagStatsDTO> tagStatsDto = dmUserTagStatsRepository.findByUserId(userId).stream()
                .map(stats -> new TagStatsDTO(stats.getUserId(), stats.getTag(), stats.getCount()))
                .collect(Collectors.toList());

        // 5. dm_user_review_word 조회 (명시적 쿼리 사용)
        List<ReviewWordDTO> reviewWordsDto = dmUserReviewWordRepository.findByUserId(userId).stream()
                .map(word -> new ReviewWordDTO(word.getUserId(), word.getWord(), word.getCount()))
                .collect(Collectors.toList());

        // 6. dm_user_insight 조회 (명시적 쿼리 사용)
        UserInsightDTO insightDto = dmUserInsightRepository.findByUserId(userId)
                .map(insight -> new UserInsightDTO(insight.getUserId(), insight.getInsight()))
                .orElse(new UserInsightDTO(userId, null));

        // 7. dm_user_group_comparison 조회 (명시적 쿼리 사용)
        UserGroupComparisonDTO comparisonDto = dmUserDiversityComparisonRepository.findByUserIdAndGroupType(userId, GroupType.all)
                .map(comp -> new UserGroupComparisonDTO(
                        comp.getUserId(),
                        comp.getGroupType().name(),
                        comp.getUserAvgScore(),
                        comp.getUserDiversityScore(),
                        comp.getGroupAvgScore(),
                        comp.getGroupDiversityScore()))
                .orElse(new UserGroupComparisonDTO(userId, GroupType.all.name(), null, null, null, null));

        // 8. 최종 응답 DTO 빌드
        return PersonalAnalysisResponse.builder()
                .dm_user_summary(summaryDto)
                .dm_user_food_rating_rank_best(bestRanksDto)
                .dm_user_food_rating_rank_worst(worstRanksDto)
                .dm_user_category_stats(categoryStatsDto)
                .dm_user_tag_stats(tagStatsDto)
                .dm_user_review_word(reviewWordsDto)
                .dm_user_insight(insightDto)
                .dm_user_group_comparison(comparisonDto)
                .build();
    }
}