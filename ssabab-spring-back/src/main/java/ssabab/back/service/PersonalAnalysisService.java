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
@RequiredArgsConstructor // 생성자 주입을 위해 사용
public class PersonalAnalysisService {

    // 개별 JPA Repository들을 주입받습니다.
    private final DmUserSummaryRepository dmUserSummaryRepository;
    private final DmUserFoodRatingRankRepository dmUserFoodRatingRankRepository;
    private final DmUserCategoryStatsRepository dmUserCategoryStatsRepository;
    private final DmUserReviewWordRepository dmUserReviewWordRepository;
    private final DmUserTagStatsRepository dmUserTagStatsRepository;
    private final DmUserInsightRepository dmUserInsightRepository;
    private final DmUserDiversityComparisonRepository dmUserDiversityComparisonRepository;

    public PersonalAnalysisResponse getPersonalAnalysis(Long userId) {
        // 1. dm_user_summary 조회 및 DTO 변환
        RatingDataDTO summaryDto = dmUserSummaryRepository.findById(userId)
                .map(summary -> RatingDataDTO.builder()
                        .userId(summary.getUserId())
                        .avgScore(summary.getAvgScore())
                        .totalReviews(summary.getTotalReviews())
                        .preVoteCount(summary.getPreVoteCount())
                        .build())
                .orElseThrow(() -> new NoSuchElementException("사용자 요약 정보를 찾을 수 없습니다. (userId: " + userId + ")"));

        // 2. dm_user_food_rating_rank 조회 및 Best/Worst 분리, DTO 변환
        List<DmUserFoodRatingRank> allRanks = dmUserFoodRatingRankRepository.findByUserId(userId);

        List<FoodRatingRankDTO> bestRanksDto = allRanks.stream()
                .filter(rank -> rank.getScoreType() == ScoreType.best)
                .map(rank -> new FoodRatingRankDTO(rank.getUserId(), rank.getFoodName(), rank.getFoodScore(), rank.getRankOrder(), rank.getScoreType()))
                .collect(Collectors.toList());

        List<FoodRatingRankDTO> worstRanksDto = allRanks.stream()
                .filter(rank -> rank.getScoreType() == ScoreType.worst)
                .map(rank -> new FoodRatingRankDTO(rank.getUserId(), rank.getFoodName(), rank.getFoodScore(), rank.getRankOrder(), rank.getScoreType()))
                .collect(Collectors.toList());

        // 3. dm_user_category_stats 조회 및 DTO 변환
        List<CategoryStatsDTO> categoryStatsDto = dmUserCategoryStatsRepository.findByUserId(userId).stream()
                .map(stats -> new CategoryStatsDTO(stats.getUserId(), stats.getCategory(), stats.getCount()))
                .collect(Collectors.toList());
        // 4. dm_user_tag_stats 조회 및 DTO 변환
        List<TagStatsDTO> tagStatsDto = dmUserTagStatsRepository.findByUserId(userId).stream()
                .map(stats -> new TagStatsDTO(stats.getUserId(), stats.getTag(), stats.getCount()))
                .collect(Collectors.toList());
        // 5. dm_user_review_word 조회 및 DTO 변환
        List<ReviewWordDTO> reviewWordsDto = dmUserReviewWordRepository.findByUserId(userId).stream()
                .map(word -> new ReviewWordDTO(word.getUserId(), word.getWord(), word.getCount()))
                .collect(Collectors.toList());

        // 6. dm_user_insight 조회 및 DTO 변환
        UserInsightDTO insightDto = dmUserInsightRepository.findById(userId)
                .map(insight -> new UserInsightDTO(insight.getUserId(), insight.getInsight()))
                .orElse(new UserInsightDTO(userId, null)); // 데이터가 없을 경우

        // 7. dm_user_group_comparison 조회 및 DTO 변환
        UserGroupComparisonDTO comparisonDto = dmUserDiversityComparisonRepository.findByUserIdAndGroupType(userId, GroupType.all)
                .map(comp -> new UserGroupComparisonDTO(comp.getUserId(), comp.getGroupType().name(), comp.getUserAvgScore(), comp.getUserDiversityScore(), comp.getGroupAvgScore(), comp.getGroupDiversityScore()))
                .orElse(new UserGroupComparisonDTO(userId, GroupType.all.name(), null, null, null, null)); // 데이터가 없을 경우

        // 8. 최종 응답 DTO 빌드 (순서 번호 변경)
        return PersonalAnalysisResponse.builder()
                .dm_user_summary(summaryDto)
                .dm_user_food_rating_rank_best(bestRanksDto)
                .dm_user_food_rating_rank_worst(worstRanksDto)
                .dm_user_category_stats(categoryStatsDto)
                .dm_user_tag_stats(tagStatsDto) // --- [추가된 필드] ---
                .dm_user_review_word(reviewWordsDto)
                .dm_user_insight(insightDto)
                .dm_user_group_comparison(comparisonDto)
                .build();
    }
}