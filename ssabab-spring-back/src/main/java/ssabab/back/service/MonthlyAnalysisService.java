// service/MonthlyAnalysisService.java
package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.*;
import ssabab.back.entity.*;
import ssabab.back.repository.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MonthlyAnalysisService {

    private final MonthlyFoodRankingRepository monthlyFoodRankingRepository;
    private final MonthlyCountRepository monthlyCountRepository;
    private final MonthlyStatisticRepository monthlyStatisticRepository;
    private final MonthlyFrequentEvaluatorRepository monthlyFrequentEvaluatorRepository;

    public MonthlyAnalysisResponse getMonthlyAnalysisData() {
        // Top 5 Foods
        List<TopFoodDTO> topFoods = monthlyFoodRankingRepository.findByRankTypeOrderByRankAsc("best").stream()
                .limit(5)
                .map(r -> new TopFoodDTO(r.getFoodName(), r.getCount(), r.getAvgScore().doubleValue()))
                .collect(Collectors.toList());

        // Worst 5 Foods
        List<TopFoodDTO> worstFoods = monthlyFoodRankingRepository.findByRankTypeOrderByRankAsc("worst").stream()
                .limit(5)
                .map(r -> new TopFoodDTO(r.getFoodName(), r.getCount(), r.getAvgScore().doubleValue()))
                .collect(Collectors.toList());

        // 월별 방문자 및 평가 수 데이터 (현재 월과 이전 월 모두 조회 필요)
        LocalDate today = LocalDate.now();
        int currentMonthValue = today.getMonthValue();
        int previousMonthValue = today.minusMonths(1).getMonthValue();

        MonthlyCount currentMonthCount = monthlyCountRepository.findById(Long.valueOf(currentMonthValue)).orElse(new MonthlyCount(Long.valueOf(currentMonthValue), 0L, 0L, 0L));
        MonthlyCount previousMonthCount = monthlyCountRepository.findById(Long.valueOf(previousMonthValue)).orElse(new MonthlyCount(Long.valueOf(previousMonthValue), 0L, 0L, 0L));

        // 월별 방문자
        MonthlyVisitorsDTO monthlyVisitors = MonthlyVisitorsDTO.builder()
                .current(currentMonthCount.getEvaluator())
                .previous(previousMonthCount.getEvaluator())
                .totalCumulative(currentMonthCount.getCumulative())
                .previousMonthCumulative(currentMonthCount.getCumulative() - currentMonthCount.getDifference()) // 이전달 누적 = 현재 누적 - 현재 증가분
                .build();

        // 누적 평가
        CumulativeEvaluationsDTO cumulativeEvaluations = CumulativeEvaluationsDTO.builder()
                .currentMonth(currentMonthCount.getEvaluator())
                .totalCumulative(currentMonthCount.getCumulative())
                .previousMonthCumulative(currentMonthCount.getCumulative() - currentMonthCount.getDifference())
                .build();

        // 평점 분포
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

        // Frequent Visitors
        List<FrequentVisitorDTO> frequentVisitors = monthlyFrequentEvaluatorRepository.findAllByOrderByRankAsc().stream()
                .limit(5)
                .map(f -> {
                    String formattedDate = f.getLastEvaluate() != null ? f.getLastEvaluate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "N/A";
                    return new FrequentVisitorDTO(f.getName(), f.getEvaluates(), formattedDate);
                })
                .collect(Collectors.toList());

        // 월간 전체 평점
        MonthlyOverallRatingDTO monthlyOverallRating = MonthlyOverallRatingDTO.builder()
                .average(stats.getAvg() != null ? stats.getAvg().doubleValue() : 0.0)
                .totalEvaluations(currentMonthCount.getEvaluator())
                .build();

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