// ssabab/back/dto/MonthlyDashboardResponse.java
package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyDashboardResponse {
    private List<FoodRankingDTO> bestFoods; // 월 Best 음식 Top 5
    private List<FoodRankingDTO> worstFoods; // 월 Worst 음식 Top 5
    private List<UserProfileStatsDTO> userProfileStats; // 기수, 반, 성별, 나이 분포
    private int monthlyVisitorCount; // 월간 방문자 수
    private String eventWinner; // 이벤트 당첨자 (최빈 투표)
    private List<VoteCountDTO> voteCounts; // 월간 사전 투표 수 (각 메뉴별로 필요)
}