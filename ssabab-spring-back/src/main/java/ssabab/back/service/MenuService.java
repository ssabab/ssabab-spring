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
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMenusByDate(LocalDate date) {
        List<Menu> menus = menuRepository.findByDateOrderByMenuIdAsc(date);

        return menus.stream().map(menu -> {
            Map<String, Object> menuMap = new LinkedHashMap<>();
            menuMap.put("menuId", menu.getMenuId());
            List<FoodResponseDTO> foodDTOs = menu.getFoods().stream()
                    .map(FoodResponseDTO::from)
                    .toList();
            menuMap.put("foods", foodDTOs);
            return menuMap;
        }).toList();
    }

    /**
     * 현재 주차(지난주 월요일 ~ 이번 주 금요일)의 전체 메뉴를 요일별로 반환합니다.
     */
    @Transactional(readOnly = true)
    public WeeklyMenuResponse getWeeklyMenus() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
        LocalDate endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        List<Menu> menusInPeriod = menuRepository.findByDateBetweenOrderByDateAscMenuIdAsc(startDate, endDate);

        Map<LocalDate, DailyMenuResponse.DailyMenuResponseBuilder> dailyMenuBuilders = new LinkedHashMap<>();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dailyMenuBuilders.put(d, DailyMenuResponse.builder().date(d).menu1(MenuResponseDTO.empty()).menu2(MenuResponseDTO.empty()));
            }
        }

        Map<LocalDate, Integer> menuCountMap = new HashMap<>();
        for (Menu menu : menusInPeriod) {
            LocalDate menuDate = menu.getDate();
            int currentCount = menuCountMap.getOrDefault(menuDate, 0);
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

        List<DailyMenuResponse> weeklyMenus = dailyMenuBuilders.values().stream()
                .map(DailyMenuResponse.DailyMenuResponseBuilder::build)
                .collect(Collectors.toList());

        return WeeklyMenuResponse.builder().weeklyMenus(weeklyMenus).build();
    }

    /**
     * 관리자가 새로운 일간 메뉴 2개를 등록합니다.
     */
    @Transactional
    public void saveDailyMenus(LocalDate date, List<MenuBlockRequest> menuBlocks) {
        Account user = getLoginUser();
        validateAdmin(user);

        if (menuBlocks == null || menuBlocks.size() != 2) {
            throw new IllegalArgumentException("2개의 메뉴 블록이 필요합니다.");
        }
        if (menuRepository.findByDate(date).size() >= 2) {
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

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다: " + menuId));

        List<MenuFood> existingLinks = menu.getMenuFoods();
        if (existingLinks != null && !existingLinks.isEmpty()) {
            menuFoodRepository.deleteAll(existingLinks);
            existingLinks.clear();
        } else {
            menu.setMenuFoods(new ArrayList<>());
        }

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

            MenuFood link = new MenuFood(menu, food);
            menu.getMenuFoods().add(link);
        }

        menuRepository.save(menu);
    }

    /**
     * 관리자가 특정 날짜의 모든 메뉴를 삭제합니다.
     */
    @Transactional
    public void deleteDailyMenus(LocalDate date) {
        Account user = getLoginUser();
        validateAdmin(user);

        List<Menu> menus = menuRepository.findByDate(date);
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
