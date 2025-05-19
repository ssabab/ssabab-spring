package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponseDTO {
    private Map<String, MonthStat> monthlyStats;
    private List<MenuStat> bestMenus;
    private List<MenuStat> worstMenus;
    private PersonalStat personalStat;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthStat {
        private int total;
        private Map<String, Integer> classCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuStat {
        private String menuName;
        private double averageRating;
        private Double userRating; // 개인 통계용
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagDistribution {
        private String category;
        private List<TagStat> tags;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagStat {
        private String tag;
        private int percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalStat {
        private double userAverageRating;
        private double globalAverageRating;
        private List<TagDistribution> tagDistribution;
        private List<MenuStat> bestMenus;
        private List<MenuStat> worstMenus;
    }
} 