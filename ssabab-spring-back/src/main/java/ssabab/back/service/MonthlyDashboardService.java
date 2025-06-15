// ssabab/back/service/MonthlyDashboardService.java
package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.FoodRankingDTO; // DTO 이름 통일: FoodRankingDTO -> FoodRankingDto
import ssabab.back.dto.MonthlyDashboardResponse;
import ssabab.back.dto.UserProfileStatsDTO; // DTO 이름 통일: UserProfileStatsDTO -> UserProfileStatsDto
import ssabab.back.dto.VoteCountDTO; // DTO 이름 통일: VoteCountDTO -> VoteCountDto
import ssabab.back.entity.DmMonthlyClassEngagement;
import ssabab.back.entity.DmMonthlyVisitorCount;
import ssabab.back.entity.DmMonthlyVoteCount;
import ssabab.back.repository.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlyDashboardService {

    private final DmMonthlyFoodRankingRepository monthlyFoodRankingRepository;
    private final DmMonthlyVoteCountRepository monthlyVoteCountRepository;
    private final DmMonthlyVisitorCountRepository monthlyVisitorCountRepository;
    private final DmMonthlyClassEngagementRepository monthlyClassEngagementRepository; // ERD에 있으므로 추가
    // private final MenuRepository menuRepository; // 메뉴 이름 조회를 위해 추가 (현재 사용되지 않아 주석 처리)

    @Transactional(readOnly = true)
    public MonthlyDashboardResponse getDashboard() {
        int currentMonth = LocalDate.now().getMonthValue();

        // 1. 월 Best & Worst 음식 각 Top 5 (ERD: dm_monthly_food_ranking)
        List<FoodRankingDTO> bestFoods = monthlyFoodRankingRepository.findByMonthAndRankTypeOrderByRankAsc(currentMonth, "best")
                .stream()
                .map(dm -> FoodRankingDTO.builder()
                        .foodName(dm.getFoodName())
                        .avgScore(dm.getAvgScore()) // dm.getAvgScore()는 Double 타입
                        .rank(dm.getRank())
                        .build())
                .collect(Collectors.toList());

        List<FoodRankingDTO> worstFoods = monthlyFoodRankingRepository.findByMonthAndRankTypeOrderByRankAsc(currentMonth, "worst")
                .stream()
                .map(dm -> FoodRankingDTO.builder()
                        .foodName(dm.getFoodName())
                        .avgScore(dm.getAvgScore()) // dm.getAvgScore()는 Double 타입
                        .rank(dm.getRank())
                        .build())
                .collect(Collectors.toList());

        // 2. 기수, 반, 성별, 나이 분포 (ERD: dm_monthly_vote_count)
        List<DmMonthlyVoteCount> monthlyVoteCounts = monthlyVoteCountRepository.findByMonth(currentMonth);
        List<UserProfileStatsDTO> userProfileStats = compileMonthlyUserProfileStats(monthlyVoteCounts);

        // 3. 월간 방문자 수 (ERD: dm_monthly_visitors)
        int monthlyVisitorCount = monthlyVisitorCountRepository.findByMonth(currentMonth)
                .map(DmMonthlyVisitorCount::getUserCount)
                .orElse(0);

        // 4. 이벤트 당첨자(최빈 투표) (SRS에 명시, ERD에서 dm_monthly_vote_count review_count로 추정)
        // 이 부분은 최빈 투표자에 대한 로직이 별도로 필요합니다.
        // 현재 DM 테이블에 '이벤트 당첨자' 필드가 없으므로, 여기서는 해당 월의 최다 리뷰 카운트를 가진 계층을 임시로 보여줍니다.
        // 실제 로직은 별도의 데이터 집계 과정(혹은 별도의 이벤트 당첨자 DM 테이블)이 필요합니다.
        String eventWinner = findEventWinner(monthlyVoteCounts);


        // 5. 월간 사전 투표 수 (각 메뉴별로 필요)
        // ERD 상 dm_monthly_vote_count는 프로필 분포만 있고 메뉴별 투표수는 직접적으로 보이지 않습니다.
        // 이 데이터가 필요하다면 별도의 DM 테이블 (예: dm_monthly_menu_vote_count)이 필요합니다.
        // 임시로 월별 클래스별 총 리뷰 수를 보여주는 것으로 대체합니다. (정확한 SRS 요구사항이 불명확)
        List<VoteCountDTO> voteCounts = compileMonthlyVoteCounts(currentMonth);


        return MonthlyDashboardResponse.builder()
                .bestFoods(bestFoods)
                .worstFoods(worstFoods)
                .userProfileStats(userProfileStats)
                .monthlyVisitorCount(monthlyVisitorCount)
                .eventWinner(eventWinner)
                .voteCounts(voteCounts)
                .build();
    }

    private List<UserProfileStatsDTO> compileMonthlyUserProfileStats(List<DmMonthlyVoteCount> monthlyVoteCounts) {
        List<UserProfileStatsDTO> stats = new java.util.ArrayList<>();

        // 원본 totalReviewCount를 계산합니다.
        long rawTotalReviewCount = monthlyVoteCounts.stream()
                .filter(d -> d.getReviewCount() != null)
                .mapToInt(DmMonthlyVoteCount::getReviewCount)
                .sum();

        // 람다에서 참조할 최종(effectively final) double 타입 변수를 선언하고 값을 할당합니다.
        // 이 변수는 한 번만 할당되며, 이후 변경되지 않으므로 final 속성을 만족합니다.
        final double safeTotalReviewCount = (rawTotalReviewCount == 0) ? 1.0 : (double) rawTotalReviewCount;

        // 기수별 통계
        Map<String, Long> generationCounts = monthlyVoteCounts.stream()
                .filter(d -> d.getGeneration() != null && d.getReviewCount() != null)
                .collect(Collectors.groupingBy(DmMonthlyVoteCount::getGeneration,
                        Collectors.summingLong(DmMonthlyVoteCount::getReviewCount)));
        generationCounts.forEach((gen, count) -> stats.add(UserProfileStatsDTO.builder()
                .type("generation")
                .value(gen)
                .count(count)
                .percentage((double) count / safeTotalReviewCount * 100) // safeTotalReviewCount 사용
                .build()));

        // 반별 통계
        Map<String, Long> classNumCounts = monthlyVoteCounts.stream()
                .filter(d -> d.getClassNum() != null && d.getReviewCount() != null)
                .collect(Collectors.groupingBy(DmMonthlyVoteCount::getClassNum,
                        Collectors.summingLong(DmMonthlyVoteCount::getReviewCount)));
        classNumCounts.forEach((classNum, count) -> stats.add(UserProfileStatsDTO.builder()
                .type("classNum")
                .value(classNum)
                .count(count)
                .percentage((double) count / safeTotalReviewCount * 100) // safeTotalReviewCount 사용
                .build()));

        // 성별 통계
        Map<String, Long> genderCounts = monthlyVoteCounts.stream()
                .filter(d -> d.getGender() != null && d.getReviewCount() != null)
                .collect(Collectors.groupingBy(DmMonthlyVoteCount::getGender,
                        Collectors.summingLong(DmMonthlyVoteCount::getReviewCount)));
        genderCounts.forEach((gender, count) -> stats.add(UserProfileStatsDTO.builder()
                .type("gender")
                .value(gender)
                .count(count)
                .percentage((double) count / safeTotalReviewCount * 100) // safeTotalReviewCount 사용
                .build()));

        // 나이별 통계
        Map<String, Long> ageCounts = monthlyVoteCounts.stream()
                .filter(d -> d.getAge() != null && d.getReviewCount() != null)
                .collect(Collectors.groupingBy(d -> getAgeGroup(d.getAge()),
                        Collectors.summingLong(DmMonthlyVoteCount::getReviewCount)));
        ageCounts.forEach((ageGroup, count) -> stats.add(UserProfileStatsDTO.builder()
                .type("age")
                .value(ageGroup)
                .count(count)
                .percentage((double) count / safeTotalReviewCount * 100) // safeTotalReviewCount 사용
                .build()));

        return stats;
    }

    // 나이대를 그룹화하는 헬퍼 메서드
    private String getAgeGroup(Integer age) {
        if (age == null) return "미상";
        if (age < 20) return "10대 미만";
        if (age < 30) return "20대";
        if (age < 40) return "30대";
        if (age < 50) return "40대";
        return "50대 이상";
    }

    private String findEventWinner(List<DmMonthlyVoteCount> monthlyVoteCounts) {
        // DM_MONTHLY_VOTE_COUNT 테이블의 review_count가 "최빈 투표"에 대한 지표라고 가정합니다.
        // 여기서는 가장 높은 review_count를 가진 세그먼트(예: 기수, 반)를 이벤트 당첨자로 간주하는 예시를 보여줍니다.
        // 실제 "이벤트 당첨자(최빈 투표)"의 기준이 명확하지 않으므로, 이 부분은 SRS에 따라 변경될 수 있습니다.
        Optional<DmMonthlyVoteCount> winnerSegment = monthlyVoteCounts.stream()
                .max(java.util.Comparator.comparingInt(DmMonthlyVoteCount::getReviewCount));

        return winnerSegment.map(segment -> {
            StringBuilder sb = new StringBuilder("최빈 투표자: ");
            if (segment.getGeneration() != null) sb.append(segment.getGeneration()).append("기 ");
            if (segment.getClassNum() != null) sb.append(segment.getClassNum()).append("반 ");
            if (segment.getGender() != null) sb.append(segment.getGender()).append("성 ");
            if (segment.getAge() != null) sb.append(getAgeGroup(segment.getAge())).append(" ");
            sb.append("(").append(segment.getReviewCount()).append("회 참여)");
            return sb.toString();
        }).orElse("이벤트 당첨자 없음");
    }

    private List<VoteCountDTO> compileMonthlyVoteCounts(int month) {
        // ERD 상 월간 메뉴별 투표수(dm_monthly_menu_vote_count)가 직접적으로 없어
        // dm_monthly_class_engagement의 review_count (월간 총 리뷰 수)를 메뉴별 투표수로 임시 활용하거나,
        // 이 데이터를 담을 별도의 DM 테이블이 필요합니다.
        // 여기서는 ERD에 있는 class_engagement_monthly를 활용하여 '전체 리뷰 수'를 예시로 보여줍니다.
        // 만약 메뉴별 투표수가 필요하다면, 해당 데이터를 DB에 집계하는 로직이 선행되어야 합니다.

        // ERD에 dm_monthly_vote_count에 'menu_id'가 없어 어떤 메뉴에 대한 투표인지는 알 수 없습니다.
        // 이 부분은 "친구들의 사전 투표 조회" (PreVoteService)와는 다른 월간 집계 데이터입니다.
        // ERD의 'dm_monthly_vote_count'는 프로필별 투표/리뷰 횟수를 나타내는 것으로 보입니다.

        // 따라서 이 부분은 현재 DM ERD만으로는 정확한 "월간 메뉴별 투표 수"를 집계하기 어렵습니다.
        // 임시로 월별 클래스별 총 리뷰 수를 보여주는 것으로 대체합니다. (정확한 SRS 요구사항이 불명확)
        List<DmMonthlyClassEngagement> classEngagements = monthlyClassEngagementRepository.findByMonth(month);

        return classEngagements.stream()
                .map(ce -> {
                    // 메뉴ID와 메뉴 이름을 특정할 수 없으므로 더미 값 사용
                    return VoteCountDTO.builder()
                            .menuId(null) // 특정 메뉴 ID 없음
                            .menuName(String.format("월간 %s %s반 총 참여", ce.getGeneration(), ce.getClassNum()))
                            .foods(Collections.emptyList()) // 특정 음식 정보 없음
                            .voteCount((long) ce.getReviewCount())
                            .build();
                })
                .collect(Collectors.toList());

        // 만약 '월간 사전 투표 수'가 각 메뉴 (A, B)에 대한 총 투표수를 의미한다면,
        // 해당 데이터가 집계되는 별도의 DM 테이블 (예: dm_monthly_menu_vote_summary)이 필요합니다.
    }
}
