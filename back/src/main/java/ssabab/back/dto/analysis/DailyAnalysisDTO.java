package ssabab.back.dto.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyAnalysisDTO {
    private MenuDataDTO menu;
    private VoteResultDTO voteResult;
    private VoteResultDTO actualSelectionResult;
    private Map<String, GenerationDataDTO> generation;
    private Map<String, GenderDataDTO> gender;
    private Map<String, AgeDataDTO> age;
    private List<KeywordDTO> keyword;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MenuDataDTO {
        private MenuItemDTO menuA;
        private MenuItemDTO menuB;
        
        // JSON 매핑을 위한 getter/setter
        @JsonProperty("A")
        public MenuItemDTO getMenuA() {
            return menuA;
        }
        
        @JsonProperty("A")
        public void setMenuA(MenuItemDTO menuA) {
            this.menuA = menuA;
        }
        
        @JsonProperty("B")
        public MenuItemDTO getMenuB() {
            return menuB;
        }
        
        @JsonProperty("B")
        public void setMenuB(MenuItemDTO menuB) {
            this.menuB = menuB;
        }
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MenuItemDTO {
        private List<FoodItemDTO> foods;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FoodItemDTO {
        private String foodName;
        private String mainSub;
        private String category;
        private String tag;
        private double rating;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VoteResultDTO {
        private int countA;
        private int countB;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GenerationDataDTO {
        private int countA;
        private int countB;
        private Map<String, ClassDataDTO> classData;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ClassDataDTO {
        private int countA;
        private int countB;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GenderDataDTO {
        private int countA;
        private int countB;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AgeDataDTO {
        private int countA;
        private int countB;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class KeywordDTO {
        private String word;
        private int count;
    }
} 