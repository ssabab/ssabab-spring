package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.dto.analysis.DailyAnalysisDTO;
import ssabab.back.dto.analysis.MonthlyAnalysisDTO;
import ssabab.back.dto.analysis.PersonalAnalysisDTO;
import ssabab.back.repository.AccountRepository;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AccountRepository accountRepository;

    /**
     * 개인 분석 데이터를 조회합니다.
     * @param userId 사용자 ID
     * @return 개인 분석 데이터
     */
    public PersonalAnalysisDTO getPersonalAnalysis(Integer userId) {
        // 사용자 검증
        accountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 샘플 데이터 생성 및 반환
        List<PersonalAnalysisDTO.TagCategoryDTO> tagDistribution = Arrays.asList(
            new PersonalAnalysisDTO.TagCategoryDTO("한식", Arrays.asList(
                new PersonalAnalysisDTO.TagPercentageDTO("매콤한", 60),
                new PersonalAnalysisDTO.TagPercentageDTO("고소한", 30),
                new PersonalAnalysisDTO.TagPercentageDTO("달콤한", 10)
            )),
            new PersonalAnalysisDTO.TagCategoryDTO("중식", Arrays.asList(
                new PersonalAnalysisDTO.TagPercentageDTO("매콤한", 40),
                new PersonalAnalysisDTO.TagPercentageDTO("짭짤한", 40),
                new PersonalAnalysisDTO.TagPercentageDTO("달콤한", 20)
            ))
        );

        List<PersonalAnalysisDTO.RatedMenuDTO> bestMenus = Arrays.asList(
            new PersonalAnalysisDTO.RatedMenuDTO("김치찌개", 4.8),
            new PersonalAnalysisDTO.RatedMenuDTO("된장찌개", 4.5),
            new PersonalAnalysisDTO.RatedMenuDTO("짜장면", 4.2)
        );

        List<PersonalAnalysisDTO.RatedMenuDTO> worstMenus = Arrays.asList(
            new PersonalAnalysisDTO.RatedMenuDTO("생선까스", 2.1),
            new PersonalAnalysisDTO.RatedMenuDTO("햄버거", 2.3),
            new PersonalAnalysisDTO.RatedMenuDTO("냉면", 2.5)
        );

        return PersonalAnalysisDTO.builder()
                .userAverageRating(3.7)
                .globalAverageRating(3.5)
                .tagDistribution(tagDistribution)
                .bestMenus(bestMenus)
                .worstMenus(worstMenus)
                .build();
    }

    /**
     * 월별 분석 데이터를 조회합니다.
     * @param yearMonth 년월 (예: 2025-05)
     * @return 월별 분석 데이터
     */
    public MonthlyAnalysisDTO getMonthlyAnalysis(String yearMonth) {
        // 입력 형식이 2025-05 형식으로 들어옴
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        
        // 샘플 데이터 생성 및 반환
        Map<String, MonthlyAnalysisDTO.GenerationDataDTO> generationData = new HashMap<>();
        
        Map<String, Integer> classData1 = new HashMap<>();
        classData1.put("A", 25);
        classData1.put("B", 30);
        classData1.put("C", 20);
        generationData.put("10기", new MonthlyAnalysisDTO.GenerationDataDTO(75, classData1));
        
        Map<String, Integer> classData2 = new HashMap<>();
        classData2.put("A", 28);
        classData2.put("B", 22);
        classData2.put("C", 25);
        generationData.put("11기", new MonthlyAnalysisDTO.GenerationDataDTO(75, classData2));
        
        List<MonthlyAnalysisDTO.RatedMenuDTO> bestMenus = Arrays.asList(
            new MonthlyAnalysisDTO.RatedMenuDTO("김치찌개", 4.5),
            new MonthlyAnalysisDTO.RatedMenuDTO("비빔밥", 4.3),
            new MonthlyAnalysisDTO.RatedMenuDTO("짜장면", 4.2)
        );
        
        List<MonthlyAnalysisDTO.RatedMenuDTO> worstMenus = Arrays.asList(
            new MonthlyAnalysisDTO.RatedMenuDTO("돈까스", 2.5),
            new MonthlyAnalysisDTO.RatedMenuDTO("냉면", 2.7),
            new MonthlyAnalysisDTO.RatedMenuDTO("볶음밥", 2.9)
        );
        
        return MonthlyAnalysisDTO.builder()
                .generationData(generationData)
                .bestMenus(bestMenus)
                .worstMenus(worstMenus)
                .build();
    }

    /**
     * 일별 분석 데이터를 조회합니다.
     * @param date 날짜 (예: 2024-04-20)
     * @return 일별 분석 데이터
     */
    public DailyAnalysisDTO getDailyAnalysis(LocalDate date) {
        // 샘플 메뉴 A 생성
        List<DailyAnalysisDTO.FoodItemDTO> foodsA = Arrays.asList(
            new DailyAnalysisDTO.FoodItemDTO("김치찌개", "main", "한식", "매콤한", 4.2),
            new DailyAnalysisDTO.FoodItemDTO("밥", "sub", "한식", "담백한", 4.0),
            new DailyAnalysisDTO.FoodItemDTO("계란말이", "sub", "한식", "담백한", 4.1),
            new DailyAnalysisDTO.FoodItemDTO("무생채", "sub", "한식", "아삭한", 3.8)
        );
        
        // 샘플 메뉴 B 생성
        List<DailyAnalysisDTO.FoodItemDTO> foodsB = Arrays.asList(
            new DailyAnalysisDTO.FoodItemDTO("짜장면", "main", "중식", "담백한", 4.0),
            new DailyAnalysisDTO.FoodItemDTO("단무지", "sub", "중식", "새콤한", 3.5),
            new DailyAnalysisDTO.FoodItemDTO("춘장", "sub", "중식", "담백한", 3.7),
            new DailyAnalysisDTO.FoodItemDTO("군만두", "sub", "중식", "고소한", 4.2)
        );
        
        DailyAnalysisDTO.MenuItemDTO menuA = DailyAnalysisDTO.MenuItemDTO.builder().foods(foodsA).build();
        DailyAnalysisDTO.MenuItemDTO menuB = DailyAnalysisDTO.MenuItemDTO.builder().foods(foodsB).build();
        DailyAnalysisDTO.MenuDataDTO menu = DailyAnalysisDTO.MenuDataDTO.builder().menuA(menuA).menuB(menuB).build();
        
        // 투표 결과 샘플 데이터
        DailyAnalysisDTO.VoteResultDTO voteResult = new DailyAnalysisDTO.VoteResultDTO(65, 35);
        DailyAnalysisDTO.VoteResultDTO actualSelectionResult = new DailyAnalysisDTO.VoteResultDTO(60, 40);
        
        // 세대별 데이터
        Map<String, DailyAnalysisDTO.GenerationDataDTO> generation = new HashMap<>();
        
        Map<String, DailyAnalysisDTO.ClassDataDTO> classData10 = new HashMap<>();
        classData10.put("A", new DailyAnalysisDTO.ClassDataDTO(15, 10));
        classData10.put("B", new DailyAnalysisDTO.ClassDataDTO(20, 5));
        generation.put("10기", new DailyAnalysisDTO.GenerationDataDTO(35, 15, classData10));
        
        Map<String, DailyAnalysisDTO.ClassDataDTO> classData11 = new HashMap<>();
        classData11.put("A", new DailyAnalysisDTO.ClassDataDTO(10, 10));
        classData11.put("B", new DailyAnalysisDTO.ClassDataDTO(20, 10));
        generation.put("11기", new DailyAnalysisDTO.GenerationDataDTO(30, 20, classData11));
        
        // 성별 데이터
        Map<String, DailyAnalysisDTO.GenderDataDTO> gender = new HashMap<>();
        gender.put("남성", new DailyAnalysisDTO.GenderDataDTO(40, 20));
        gender.put("여성", new DailyAnalysisDTO.GenderDataDTO(25, 15));
        
        // 연령별 데이터
        Map<String, DailyAnalysisDTO.AgeDataDTO> age = new HashMap<>();
        age.put("20대", new DailyAnalysisDTO.AgeDataDTO(35, 15));
        age.put("30대", new DailyAnalysisDTO.AgeDataDTO(25, 15));
        age.put("40대", new DailyAnalysisDTO.AgeDataDTO(5, 5));
        
        // 키워드 데이터
        List<DailyAnalysisDTO.KeywordDTO> keyword = Arrays.asList(
            new DailyAnalysisDTO.KeywordDTO("맛있어요", 25),
            new DailyAnalysisDTO.KeywordDTO("매콤해요", 15),
            new DailyAnalysisDTO.KeywordDTO("양이 많아요", 10)
        );
        
        return DailyAnalysisDTO.builder()
                .menu(menu)
                .voteResult(voteResult)
                .actualSelectionResult(actualSelectionResult)
                .generation(generation)
                .gender(gender)
                .age(age)
                .keyword(keyword)
                .build();
    }
} 