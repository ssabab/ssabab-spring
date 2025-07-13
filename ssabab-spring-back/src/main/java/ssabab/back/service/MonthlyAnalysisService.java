// service/MonthlyAnalysisService.java
package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.dto.*;
import ssabab.back.entity.MonthlyCount;
import ssabab.back.entity.MonthlyFoodRanking;
import ssabab.back.entity.MonthlyStatistic;
import ssabab.back.repository.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlyAnalysisService {

    private final MonthlyFoodRankingRepository monthlyFoodRankingRepository;
    private final MonthlyCountRepository monthlyCountRepository;
    private final MonthlyStatisticRepository monthlyStatisticRepository;
    private final MonthlyFrequentEvaluatorRepository monthlyFrequentEvaluatorRepository;

    public MonthlyAnalysisResponse getMonthlyAnalysisData() {
        // 1. Top/Worst 랭킹을 한 번의 DB 접근으로 통합 조회
        List<MonthlyFoodRanking> rankings = monthlyFoodRankingRepository.findByRankTypeInOrderByRankAsc(List.of("best", "worst"));

        // 조회된 엔티티 리스트를 메모리에서 DTO로 변환하며 best와 worst로 분리
        Map<String, List<TopFoodDTO>> rankingsByType = rankings.stream()
                .collect(Collectors.groupingBy(
                        MonthlyFoodRanking::getRankType,
                        Collectors.mapping(r -> new TopFoodDTO(r.getFoodName(), r.getCount(), r.getAvgScore().doubleValue()), Collectors.toList())
                ));

        List<TopFoodDTO> topFoods = rankingsByType.getOrDefault("best", List.of()).stream().limit(5).collect(Collectors.toList());
        List<TopFoodDTO> worstFoods = rankingsByType.getOrDefault("worst", List.of()).stream().limit(5).collect(Collectors.toList());

        // 2. 현재 달과 지난달 통계를 한 번의 DB 접근으로 통합 조회
        LocalDate today = LocalDate.now();
        Long currentMonthValue = (long) today.getMonthValue();
        Long previousMonthValue = (long) today.minusMonths(1).getMonthValue();

        Map<Long, MonthlyCount> countsByMonth = monthlyCountRepository.findByMonthIn(List.of(currentMonthValue, previousMonthValue))
                .stream()
                .collect(Collectors.toMap(MonthlyCount::getMonth, Function.identity()));

        MonthlyCount emptyCount = new MonthlyCount(0L, 0L, 0L, 0L);
        MonthlyCount currentMonthCount = countsByMonth.getOrDefault(currentMonthValue, emptyCount);
        MonthlyCount previousMonthCount = countsByMonth.getOrDefault(previousMonthValue, emptyCount);

        // 월별 방문자 DTO 구성
        MonthlyVisitorsDTO monthlyVisitors = MonthlyVisitorsDTO.builder()
                .current(currentMonthCount.getEvaluator())
                .previous(previousMonthCount.getEvaluator())
                .totalCumulative(currentMonthCount.getCumulative())
                .previousMonthCumulative(currentMonthCount.getCumulative() - currentMonthCount.getDifference())
                .build();

        // 누적 평가 DTO 구성
        CumulativeEvaluationsDTO cumulativeEvaluations = CumulativeEvaluationsDTO.builder()
                .currentMonth(currentMonthCount.getEvaluator())
                .totalCumulative(currentMonthCount.getCumulative())
                .previousMonthCumulative(currentMonthCount.getCumulative() - currentMonthCount.getDifference())
                .build();

        // 3. 나머지 통계는 이미 단일 쿼리로 효율적으로 동작
        MonthlyStatistic stats = monthlyStatisticRepository.findAll().stream().findFirst().orElse(new MonthlyStatistic());
        RatingDistributionDTO ratingDistribution = RatingDistributionDTO.builder()
                .min(stats.getMin() != null ? stats.getMin().doubleValue() : 0.0)
                .max(stats.getMax() != null ? stats.getMax().doubleValue() : 0.0)
                .avg(stats.getAvg() != null ? stats.getAvg().doubleValue() : 0.0)
                .iqrStart(stats.getQ1() != null ? stats.getQ1().doubleValue() : 0.0)
                .iqrEnd(stats.getQ3() != null ? stats.getQ3().doubleValue() : 0.0)
                .variance(stats.getVariance() != null ? stats.getVariance().doubleValue() : 0.0)
                .stdDev(stats.getStdev() != null ? stats.getStdev().doubleValue() : 0.0)
                .build();

        List<FrequentVisitorDTO> frequentVisitors = monthlyFrequentEvaluatorRepository.findAllByOrderByRankAsc().stream()
                .limit(5)
                .map(f -> {
                    String formattedDate = f.getLastEvaluate() != null ? f.getLastEvaluate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "N/A";
                    return new FrequentVisitorDTO(f.getName(), f.getEvaluates(), formattedDate);
                })
                .collect(Collectors.toList());

        MonthlyOverallRatingDTO monthlyOverallRating = MonthlyOverallRatingDTO.builder()
                .average(stats.getAvg() != null ? stats.getAvg().doubleValue() : 0.0)
                .totalEvaluations(currentMonthCount.getEvaluator())
                .build();

        // 최종 응답 DTO 빌드
        return MonthlyAnalysisResponse.builder()
                .topFoods(topFoods)
                .worstFoods(worstFoods)
                .monthlyVisitors(monthlyVisitors)
                .cumulativeEvaluations(cumulativeEvaluations)
                .ratingDistribution(ratingDistribution)
                .frequentVisitors(frequentVisitors)
                .monthlyOverallRating(monthlyOverallRating)
                .build();
    }
}