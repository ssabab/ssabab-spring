// ssabab/back/service/PersonalDashboardService.java
package ssabab.back.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.FoodPreferenceDTO;
import ssabab.back.dto.PersonalDashboardResponse;
import ssabab.back.dto.FoodRankingDTO; // FoodRankingDTO 하나만 사용
// import ssabab.back.dto.RankingFoodDTO; // <-- 이 import를 제거합니다.
import ssabab.back.entity.*;
import ssabab.back.enums.IqrType;
import ssabab.back.repository.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 개인 분석 대시보드 비즈니스 로직 서비스
 * SRS: 개인 분석(잠금) (클릭 시 로그인 요청 및 로그인 페이지 팝업)
 * -> 로그인 O 케이스에서만 접근 가능
 */
@Service
@RequiredArgsConstructor
public class PersonalDashboardService {

    private final AccountRepository accountRepository;
    private final DmUserRatingTopBottomRepository dmUserRatingTopBottomRepository;
    private final DmUserCategoryTagPreferenceRepository dmUserCategoryTagPreferenceRepository;
    private final DmUserRatingRepository dmUserRatingRepository;
    private final DmIqrsRepository dmIqrsRepository;
    private final ObjectMapper objectMapper; // JSON 파싱을 위해 추가

    @Transactional(readOnly = true)
    public PersonalDashboardResponse getDashboard() {
        Account user = getLoginUser(); // 로그인된 사용자 정보 가져오기
        Long userId = user.getUserId();

        // 1. 개인 선호 음식 (Best or Worst) - dm_user_rating_top_bottom 테이블 활용
        Optional<DmUserRatingTopBottom> topBottomOpt = dmUserRatingTopBottomRepository.findByUserId(userId);

        // List<RankingFoodDTO> bestFoods = ... 에서 List<FoodRankingDTO> bestFoods = ... 로 변경
        List<FoodRankingDTO> bestFoods = topBottomOpt.map(tb -> List.of(
                        FoodRankingDTO.builder().foodName(tb.getBestFood1Name()).avgScore((double)tb.getBestFood1Score()).build(), // Float -> Double 변환
                        FoodRankingDTO.builder().foodName(tb.getBestFood2Name()).avgScore((double)tb.getBestFood2Score()).build(),
                        FoodRankingDTO.builder().foodName(tb.getBestFood3Name()).avgScore((double)tb.getBestFood3Score()).build(),
                        FoodRankingDTO.builder().foodName(tb.getBestFood4Name()).avgScore((double)tb.getBestFood4Score()).build(),
                        FoodRankingDTO.builder().foodName(tb.getBestFood5Name()).avgScore((double)tb.getBestFood5Score()).build()
                )).orElse(Collections.emptyList())
                .stream()
                .filter(food -> food.getFoodName() != null) // null인 항목 필터링
                .collect(Collectors.toList());

        // List<RankingFoodDTO> worstFoods = ... 에서 List<FoodRankingDTO> worstFoods = ... 로 변경
        List<FoodRankingDTO> worstFoods = topBottomOpt.map(tb -> List.of(
                        FoodRankingDTO.builder().foodName(tb.getWorstFood1Name()).avgScore((double)tb.getWorstFood1Score()).build(),
                        FoodRankingDTO.builder().foodName(tb.getWorstFood2Name()).avgScore((double)tb.getWorstFood2Score()).build(),
                        FoodRankingDTO.builder().foodName(tb.getWorstFood3Name()).avgScore((double)tb.getWorstFood3Score()).build(),
                        FoodRankingDTO.builder().foodName(tb.getWorstFood4Name()).avgScore((double)tb.getWorstFood4Score()).build(),
                        FoodRankingDTO.builder().foodName(tb.getWorstFood5Name()).avgScore((double)tb.getWorstFood5Score()).build()
                )).orElse(Collections.emptyList())
                .stream()
                .filter(food -> food.getFoodName() != null) // null인 항목 필터링
                .collect(Collectors.toList());

        // 2. 최빈 카테고리 or 태그 - dm_user_category_tag_preference 테이블 활용
        Optional<DmUserCategoryTagPreference> preferenceOpt = dmUserCategoryTagPreferenceRepository.findByUserId(userId);

        List<FoodPreferenceDTO> topCategoryPreference = Collections.emptyList();
        List<FoodPreferenceDTO> topTagPreference = Collections.emptyList();

        if (preferenceOpt.isPresent()) {
            DmUserCategoryTagPreference preference = preferenceOpt.get();
            try {
                // category_json 파싱
                if (preference.getCategoryJson() != null) {
                    topCategoryPreference = objectMapper.readValue(preference.getCategoryJson(), new TypeReference<List<FoodPreferenceDTO>>() {});
                }
                // tag_json 파싱
                if (preference.getTagJson() != null) {
                    topTagPreference = objectMapper.readValue(preference.getTagJson(), new TypeReference<List<FoodPreferenceDTO>>() {});
                }
            } catch (JsonProcessingException e) {
                System.err.println("개인 선호도 JSON 파싱 오류: " + e.getMessage());
            }
        }

        // 3. 전체 평점 대비 본인 평점 비교 (엄격, 비슷, 후한) - dm_user_rating 및 dm_iqrs 테이블 활용
        String evaluationTendency = "데이터 없음";
        Optional<DmUserRating> userRatingOpt = dmUserRatingRepository.findByUserId(userId);
        Optional<DmIqrs> totalIqrsOpt = dmIqrsRepository.findByIqrType(IqrType.total);

        if (userRatingOpt.isPresent() && totalIqrsOpt.isPresent()) {
            Float userScore = userRatingOpt.get().getScore();
            DmIqrs totalIqrs = totalIqrsOpt.get();

            if (userScore != null && totalIqrs.getQ1() != null && totalIqrs.getQ2() != null && totalIqrs.getQ3() != null) {
                if (userScore < totalIqrs.getQ1()) {
                    evaluationTendency = "엄격한 편";
                } else if (userScore > totalIqrs.getQ3()) {
                    evaluationTendency = "후한 편";
                } else {
                    evaluationTendency = "비슷한 편"; // Q1과 Q3 사이
                }
            }
        }

        return PersonalDashboardResponse.builder()
                .bestFoods(bestFoods)
                .worstFoods(worstFoods)
                .topCategoryPreference(topCategoryPreference)
                .topTagPreference(topTagPreference)
                .evaluationTendency(evaluationTendency)
                .build();
    }

    /**
     * 현재 로그인한 사용자 Account 반환 (재사용)
     */
    private Account getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        String email = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // UserDetails의 username은 여기서는 email
        } else {
            throw new IllegalStateException("인증 정보에서 사용자 이메일을 찾을 수 없습니다.");
        }

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자 정보가 없습니다."));
    }
}