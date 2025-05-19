package ssabab.back.dto.analysis;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PersonalAnalysisDTO {
    private double userAverageRating;
    private double globalAverageRating;
    private List<TagCategoryDTO> tagDistribution;
    private List<RatedMenuDTO> bestMenus;
    private List<RatedMenuDTO> worstMenus;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TagCategoryDTO {
        private String category;
        private List<TagPercentageDTO> tags;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TagPercentageDTO {
        private String tag;
        private int percentage;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RatedMenuDTO {
        private String menuName;
        private double userRating;
    }
} 