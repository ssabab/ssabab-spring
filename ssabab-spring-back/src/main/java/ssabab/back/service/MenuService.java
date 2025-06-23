// service.MenuService
package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.DailyMenuResponse;
import ssabab.back.dto.MenuBlockRequest;
import ssabab.back.dto.MenuRequestDTO;
import ssabab.back.dto.MenuResponseDTO;
import ssabab.back.dto.WeeklyMenuResponse;
import ssabab.back.entity.*;
import ssabab.back.repository.AccountRepository;
import ssabab.back.repository.FoodRepository;
import ssabab.back.repository.MenuRepository;
import ssabab.back.repository.MenuFoodRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap; // 변경: HashMap 임포트 추가 (없었다면)

/**
 * 메뉴 조회 및 등록/수정 비즈니스 로직 서비스
 * (menuOrder 필드 없음 - menu_id 순서로 menu1/menu2 구분, 불안정)
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final AccountRepository accountRepository;
    private final MenuFoodRepository menuFoodRepository;

    /**
     * 특정 날짜의 메뉴 2개 조회 (menu1, menu2 형태로 보장)
     */
    @Transactional(readOnly = true)
    public DailyMenuResponse getMenusByDate(LocalDate date) {
        List<Menu> menus = menuRepository.findByDateOrderByMenuIdAsc(date);

        MenuResponseDTO menu1DTO = MenuResponseDTO.empty();
        MenuResponseDTO menu2DTO = MenuResponseDTO.empty();

        if (!menus.isEmpty()) {
            menu1DTO = MenuResponseDTO.from(menus.get(0));
            if (menus.size() > 1) {
                menu2DTO = MenuResponseDTO.from(menus.get(1));
            }
        }

        return DailyMenuResponse.builder()
                .date(date)
                .menu1(menu1DTO)
                .menu2(menu2DTO)
                .build();
    }

    /**
     * 특정 기간 (지난주 월요일 ~ 이번 주 금요일)의 메뉴 데이터를 가져옵니다.
     * 변경: 주말(토, 일) 제외
     */
    @Transactional(readOnly = true)
    public WeeklyMenuResponse getWeeklyMenus() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        List<Menu> menusInPeriod = menuRepository.findByDateBetweenOrderByDateAscMenuIdAsc(startDate, endDate);

        // 변경: Map의 값 타입을 DailyMenuResponse.Builder에서 DailyMenuResponse.DailyMenuResponseBuilder로 수정
        Map<LocalDate, DailyMenuResponse.DailyMenuResponseBuilder> dailyMenuBuilders = new LinkedHashMap<>();

        // 기간 내의 월요일부터 금요일까지만 Map에 추가하고 초기화
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dailyMenuBuilders.put(d, DailyMenuResponse.builder().date(d).menu1(MenuResponseDTO.empty()).menu2(MenuResponseDTO.empty()));
            }
        }

        // 가져온 메뉴들을 해당하는 날짜의 DailyMenuResponse Builder에 할당
        Map<LocalDate, Integer> menuCountMap = new HashMap<>();
        for (Menu menu : menusInPeriod) {
            LocalDate menuDate = menu.getDate();
            if (menuDate.getDayOfWeek() != DayOfWeek.SATURDAY && menuDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                int currentCount = menuCountMap.getOrDefault(menuDate, 0);

                // 변경: builder 변수 선언 시 타입 수정
                DailyMenuResponse.DailyMenuResponseBuilder builder = dailyMenuBuilders.get(menuDate);
                if (builder != null) {
                    if (currentCount == 0) {
                        builder.menu1(MenuResponseDTO.from(menu));
                    } else if (currentCount == 1) {
                        builder.menu2(MenuResponseDTO.from(menu));
                    }
                    menuCountMap.put(menuDate, currentCount + 1);
                }
            }
        }

        List<DailyMenuResponse> weeklyMenus = dailyMenuBuilders.values().stream()
                .map(DailyMenuResponse.DailyMenuResponseBuilder::build) // 변경: Builder 타입을 정확히 명시
                .collect(Collectors.toList());

        return WeeklyMenuResponse.builder()
                .weeklyMenus(weeklyMenus)
                .build();
    }


    /**
     * 메뉴 등록 (하루 2개 메뉴를 동시에 등록) - 관리자만 가능
     */
    @Transactional
    public void saveDailyMenus(LocalDate date, List<MenuBlockRequest> menuBlockRequests) {
        Account user = getLoginUser();
        validateAdmin(user);

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("주말(" + date.getDayOfWeek() + ")에는 메뉴를 등록할 수 없습니다.");
        }

        if (menuBlockRequests == null || menuBlockRequests.size() != 2) {
            throw new IllegalArgumentException("하루에 2개의 메뉴 블록이 정확히 필요합니다.");
        }

        if (menuRepository.findByDate(date).size() >= 2) {
            throw new IllegalArgumentException("해당 날짜(" + date + ")에 이미 메뉴가 2개 등록되어 있습니다. 메뉴 수정을 이용해주세요.");
        }

        saveSingleMenuBlock(date, menuBlockRequests.get(0).getFoods());
        saveSingleMenuBlock(date, menuBlockRequests.get(1).getFoods());
    }

    /**
     * 단일 메뉴 블록 등록을 위한 헬퍼 메서드 (내부 사용)
     */
    private void saveSingleMenuBlock(LocalDate date, List<MenuRequestDTO.FoodRequestDTO> foodRequests) {
        List<Food> foodsToAssociate = new ArrayList<>();
        for (MenuRequestDTO.FoodRequestDTO foodReq : foodRequests) {
            Food food = Food.builder()
                    .foodName(foodReq.getFoodName())
                    .mainSub(foodReq.getMainSub())
                    .category(foodReq.getCategory())
                    .tag(foodReq.getTag())
                    .build();
            foodsToAssociate.add(foodRepository.save(food));
        }

        Menu menu = Menu.builder()
                .date(date)
                .build();
        menuRepository.save(menu);

        for (Food food : foodsToAssociate) {
            MenuFood menuFood = new MenuFood(menu, food);
            menuFoodRepository.save(menuFood);
        }
    }


    /**
     * 메뉴 수정 - 관리자만 가능 (단일 메뉴 수정)
     */
    @Transactional
    public void updateMenu(Long menuId, MenuRequestDTO request) {
        Account user = getLoginUser();
        validateAdmin(user);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다: " + menuId));

        if (!menu.getDate().equals(request.getDate()) &&
                (request.getDate().getDayOfWeek() == DayOfWeek.SATURDAY || request.getDate().getDayOfWeek() == DayOfWeek.SUNDAY)) {
            throw new IllegalArgumentException("주말(" + request.getDate().getDayOfWeek() + ")으로는 메뉴를 수정할 수 없습니다.");
        }

        menu.getMenuFoods().clear();

        List<Food> foodsToAssociate = new ArrayList<>();
        for (MenuRequestDTO.FoodRequestDTO foodReq : request.getFoods()) {
            Food food = Food.builder()
                    .foodName(foodReq.getFoodName())
                    .mainSub(foodReq.getMainSub())
                    .category(foodReq.getCategory())
                    .tag(foodReq.getTag())
                    .build();
            foodsToAssociate.add(foodRepository.save(food));
        }

        for (Food food : foodsToAssociate) {
            menu.getMenuFoods().add(new MenuFood(menu, food));
        }
        menuRepository.save(menu);
    }

    /**
     * 현재 로그인한 사용자 정보 반환
     */
    private Account getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            throw new IllegalStateException("인증 정보에서 사용자 이메일을 찾을 수 없습니다.");
        }

        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자 정보가 없습니다."));
    }

    /**
     * 관리자 권한 검증
     */
    private void validateAdmin(Account user) {
        if (!"ADMIN".equals(user.getRole())) {
            throw new IllegalStateException("관리자만 사용할 수 있는 기능입니다.");
        }
    }
}