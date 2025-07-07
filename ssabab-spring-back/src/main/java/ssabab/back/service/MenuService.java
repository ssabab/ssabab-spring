package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.*;
import ssabab.back.entity.*;
import ssabab.back.repository.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final AccountRepository accountRepository;
    private final MenuFoodRepository menuFoodRepository;

    /**
     * 특정 날짜의 메뉴 목록을 조회합니다.
     * @Query와 JOIN FETCH를 사용하여 N+1 문제를 해결합니다.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMenusByDate(LocalDate date) {
        // 변경된 리포지토리 메서드 사용
        List<Menu> menus = menuRepository.findMenusWithFoodsByDate(date); //

        return menus.stream().map(menu -> {
            Map<String, Object> menuMap = new LinkedHashMap<>();
            menuMap.put("menuId", menu.getMenuId());
            // menu.getFoods() 호출 시 이미 로딩된 Food 정보를 사용합니다.
            List<FoodResponseDTO> foodDTOs = menu.getFoods().stream()
                    .map(FoodResponseDTO::from)
                    .toList();
            menuMap.put("foods", foodDTOs);
            return menuMap;
        }).toList();
    }

    /**
     * 현재 주차(지난주 월요일 ~ 이번 주 금요일)의 전체 메뉴를 요일별로 반환합니다.
     * @Query와 JOIN FETCH를 사용하여 N+1 문제를 해결하고, 로직을 효율적으로 개선합니다.
     */
    @Transactional(readOnly = true)
    public WeeklyMenuResponse getWeeklyMenus() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        // 변경된 리포지토리 메서드 사용
        List<Menu> menusInPeriod = menuRepository.findMenusWithFoodsByDateRange(startDate, endDate); //

        // 날짜별로 메뉴를 그룹화
        Map<LocalDate, List<Menu>> menusByDate = menusInPeriod.stream()
                .collect(Collectors.groupingBy(Menu::getDate, LinkedHashMap::new, Collectors.toList()));

        List<DailyMenuResponse> weeklyMenus = new ArrayList<>();

        // 시작일부터 종료일까지 순회하며 DailyMenuResponse 구성
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                List<Menu> dailyMenus = menusByDate.getOrDefault(d, Collections.emptyList());

                MenuResponseDTO menu1 = MenuResponseDTO.empty();
                MenuResponseDTO menu2 = MenuResponseDTO.empty();

                if (!dailyMenus.isEmpty()) {
                    // menuId 기준으로 정렬되어 있을 것이므로, 첫 번째와 두 번째 메뉴를 가져옴
                    if (dailyMenus.size() > 0) {
                        menu1 = MenuResponseDTO.from(dailyMenus.get(0));
                    }
                    if (dailyMenus.size() > 1) {
                        menu2 = MenuResponseDTO.from(dailyMenus.get(1));
                    }
                }
                weeklyMenus.add(DailyMenuResponse.builder().date(d).menu1(menu1).menu2(menu2).build());
            }
        }

        return WeeklyMenuResponse.builder().weeklyMenus(weeklyMenus).build();
    }

    /**
     * 관리자가 새로운 일간 메뉴 2개를 등록합니다.
     */
    @Transactional
    public void saveDailyMenus(LocalDate date, List<MenuBlockRequest> menuBlocks) {
        Account user = getLoginUser();
        validateAdmin(user);

        // findMenusWithFoodsByDateForAdmin 메서드를 사용하여 해당 날짜의 메뉴 존재 여부 확인
        if (menuRepository.findMenusWithFoodsByDateForAdmin(date).size() >= 2) { //
            throw new IllegalArgumentException("이미 해당 날짜에 2개의 메뉴가 존재합니다.");
        }

        for (MenuBlockRequest block : menuBlocks) {
            Menu menu = Menu.builder().date(date).menuFoods(new ArrayList<>()).build();
            menuRepository.save(menu);

            for (MenuRequestDTO.FoodRequestDTO dto : block.getFoods()) {
                Optional<Food> existing = foodRepository.findByFoodNameAndMainSubAndCategoryAndTag(
                        dto.getFoodName(), dto.getMainSub(), dto.getCategory(), dto.getTag());

                Food food = existing.orElseGet(() -> foodRepository.save(
                        Food.builder()
                                .foodName(dto.getFoodName())
                                .mainSub(dto.getMainSub())
                                .category(dto.getCategory())
                                .tag(dto.getTag())
                                .build()
                ));

                menu.getMenuFoods().add(new MenuFood(menu, food));
            }
        }
    }

    /**
     * 관리자가 특정 메뉴의 음식 목록을 수정합니다.
     */
    @Transactional
    public void updateMenu(Long menuId, List<MenuRequestDTO.FoodRequestDTO> requestFoods) {
        Account user = getLoginUser();
        validateAdmin(user);

        // findByIdWithFoods 메서드를 사용하여 Menu와 Food를 함께 가져옵니다.
        Menu menu = menuRepository.findByIdWithFoods(menuId) //
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다: " + menuId));

        // 1. Food 목록 구성
        List<Food> newFoods = new ArrayList<>();
        for (MenuRequestDTO.FoodRequestDTO dto : requestFoods) {
            Food food = foodRepository.findByFoodNameAndMainSubAndCategoryAndTag(
                            dto.getFoodName(), dto.getMainSub(), dto.getCategory(), dto.getTag())
                    .orElseGet(() -> foodRepository.save(
                            Food.builder()
                                    .foodName(dto.getFoodName())
                                    .mainSub(dto.getMainSub())
                                    .category(dto.getCategory())
                                    .tag(dto.getTag())
                                    .build()
                    ));
            newFoods.add(food);
        }

        // 2. 메뉴의 음식 목록 갱신 (기존 메뉴-음식 연결은 setFoods 내부에서 orphanRemoval로 삭제됨)
        menu.setFoods(newFoods);
        menuRepository.save(menu);
    }

    /**
     * 관리자가 특정 날짜의 모든 메뉴를 삭제합니다.
     */
    @Transactional
    public void deleteDailyMenus(LocalDate date) {
        Account user = getLoginUser();
        validateAdmin(user);

        // findMenusWithFoodsByDateForAdmin 메서드를 사용하여 삭제할 메뉴를 가져옵니다.
        List<Menu> menus = menuRepository.findMenusWithFoodsByDateForAdmin(date); //
        if (menus.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜에 삭제할 메뉴가 없습니다: " + date);
        }

        for (Menu menu : menus) {
            menuFoodRepository.deleteAll(menu.getMenuFoods());
            menuRepository.delete(menu);
        }
    }

    /**
     * 현재 인증된 사용자 정보를 데이터베이스에서 조회합니다.
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
     * 사용자 권한이 관리자(ADMIN)인지 확인합니다.
     */
    private void validateAdmin(Account user) {
        if (!"ADMIN".equals(user.getRole())) {
            throw new IllegalStateException("관리자만 사용할 수 있는 기능입니다.");
        }
    }
}