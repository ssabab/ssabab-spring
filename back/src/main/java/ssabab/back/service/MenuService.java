package ssabab.back.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.dto.FoodDTO;
import ssabab.back.dto.MenuWithFoodsDTO;
import ssabab.back.entity.Food;
import ssabab.back.entity.Menu;
import ssabab.back.entity.MenuFood;
import ssabab.back.repository.FoodRepository;
import ssabab.back.repository.MenuRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;

    // 날짜 기반 조회
    @Transactional
    public List<MenuWithFoodsDTO> getMenusByDate(LocalDate date) {
        List<Menu> menus = menuRepository.findByDate(date);
        return menus.stream().map(menu -> {
            List<FoodDTO> foodDTOs = menu.getMenuFoods().stream()
                    .map(MenuFood::getFood)
                    .map(f -> FoodDTO.builder()
                            .foodId(f.getFoodId())
                            .foodName(f.getFoodName())
                            .mainSub(f.getMainSub())
                            .category(f.getCategory())
                            .tag(f.getTag())
                            .taste(f.getTaste())
                            .spicyLevel(f.getSpicyLevel())
                            .build())
                    .collect(Collectors.toList());
            return MenuWithFoodsDTO.builder()
                    .menuId(menu.getMenuId())
                    .foods(foodDTOs)
                    .date(date)
                    .build();
        }).collect(Collectors.toList());
    }

    // 메뉴 생성 or 수정
    @Transactional
    public void createOrUpdateMenus(LocalDate date, List<MenuWithFoodsDTO> menuDTOs, boolean isUpdate) {
        // 정확히 2개의 메뉴만 허용
        if (menuDTOs.size() != 2) {
            throw new IllegalArgumentException("메뉴는 정확히 2개만 등록 가능합니다.");
        }

        // 각 메뉴에 최대 6개의 음식만 허용
        for (MenuWithFoodsDTO dto : menuDTOs) {
            if (dto.getFoods().size() > 6) {
                throw new IllegalArgumentException("각 메뉴에는 최대 6개의 음식만 등록 가능합니다.");
            }
        }

        List<Menu> existingMenus = menuRepository.findByDate(date);
        if (!isUpdate && !existingMenus.isEmpty()) {
            throw new IllegalStateException("이미 해당 날짜에 메뉴가 존재합니다. PUT 요청을 사용하세요.");
        }
        
        // 수정 모드가 아니고, 각 메뉴에 이미 2개의 음식이 등록되어 있는지 확인
        if (!isUpdate && existingMenus.size() == 2) {
            for (Menu menu : existingMenus) {
                if (menu.getMenuFoods().size() >= 2) {
                    throw new IllegalStateException("이미 메뉴에 2개 이상의 음식이 등록되어 있습니다. 추가할 수 없습니다. PUT 요청을 사용하세요.");
                }
            }
        }
        
        if (isUpdate) {
            menuRepository.deleteByDate(date);
        }
        
        for (MenuWithFoodsDTO dto : menuDTOs) {
            Menu menu = Menu.builder()
                    .date(date)
                    .build();
            List<MenuFood> menuFoods = new ArrayList<>();
            for (FoodDTO fd : dto.getFoods()) {
                Food food = findOrCreateFood(fd);
                MenuFood menuFood = new MenuFood();
                menuFood.setMenu(menu);
                menuFood.setFood(food);
                menuFoods.add(menuFood);
            }
            menu.setMenuFoods(menuFoods);
            menuRepository.save(menu);
        }
    }

    // 중복 제거를 위한 음식 조회 or 생성
    private Food findOrCreateFood(FoodDTO fd) {
        Optional<Food> existing = foodRepository
                .findByFoodNameAndCategory(
                        fd.getFoodName(),
                        fd.getCategory()
                );
        return existing.orElseGet(() -> foodRepository.save(Food.builder()
                .foodName(fd.getFoodName())
                .mainSub(fd.getMainSub())
                .category(fd.getCategory())
                .tag(fd.getTag())
                .taste(fd.getTaste())
                .spicyLevel(fd.getSpicyLevel())
                .build()));
    }

    // 삭제
    @Transactional
    public void deleteMenusByDate(LocalDate date) {
        menuRepository.deleteByDate(date);
    }
}
