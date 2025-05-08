package ssabab.back.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.dto.FoodDTO;
import ssabab.back.dto.MenuWithFoodsDTO;
import ssabab.back.entity.Food;
import ssabab.back.entity.Menu;
import ssabab.back.repository.FoodRepository;
import ssabab.back.repository.MenuRepository;

import java.time.LocalDate;
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
        return menuRepository.findByDate(date).stream()
                .map(menu -> MenuWithFoodsDTO.builder()
                        .menuId(menu.getMenuId())
                        .foods(menu.getFoods().stream()
                                .map(f -> FoodDTO.builder()
                                        .foodId(f.getFoodId())
                                        .foodName(f.getFoodName())
                                        .mainSub(f.getMainSub())
                                        .category(f.getCategory())
                                        .tag(f.getTag())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    // 메뉴 생성 or 수정
    @Transactional
    public void createOrUpdateMenus(LocalDate date, List<MenuWithFoodsDTO> menuDTOs, boolean isUpdate) {
        List<Menu> existingMenus = menuRepository.findByDate(date);

        if (!isUpdate && !existingMenus.isEmpty()) {
            throw new IllegalStateException("이미 해당 날짜에 메뉴가 존재합니다. PUT 요청을 사용하세요.");
        }

        if (isUpdate) {
            menuRepository.deleteByDate(date);
        }

        for (MenuWithFoodsDTO dto : menuDTOs) {
            List<Food> foods = dto.getFoods().stream()
                    .map(fd -> findOrCreateFood(fd))
                    .collect(Collectors.toList());

            Menu menu = Menu.builder()
                    .date(date)
                    .foods(foods)
                    .build();

            menuRepository.save(menu);
        }
    }

    // 중복 제거를 위한 음식 조회 or 생성
    private Food findOrCreateFood(FoodDTO fd) {
        Optional<Food> existing = foodRepository
                .findByFoodNameAndMainSubAndCategoryAndTag(
                        fd.getFoodName(),
                        fd.getMainSub(),
                        fd.getCategory(),
                        fd.getTag()
                );
        return existing.orElseGet(() -> foodRepository.save(Food.builder()
                .foodName(fd.getFoodName())
                .mainSub(fd.getMainSub())
                .category(fd.getCategory())
                .tag(fd.getTag())
                .build()));
    }

    // 삭제
    @Transactional
    public void deleteMenusByDate(LocalDate date) {
        menuRepository.deleteByDate(date);
    }
}
