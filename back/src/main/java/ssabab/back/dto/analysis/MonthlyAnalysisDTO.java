package ssabab.back.dto.analysis;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MonthlyAnalysisDTO {
    private Map<String, GenerationDataDTO> generationData;
    private List<RatedMenuDTO> bestMenus;
    private List<RatedMenuDTO> worstMenus;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GenerationDataDTO {
        private int total;
        private Map<String, Integer> classData;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RatedMenuDTO {
        private String menuName;
        private double averageRating;
    }
} 