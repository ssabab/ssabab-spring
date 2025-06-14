package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.MenuRequestDTO;
import ssabab.back.dto.MenuResponseDTO;
import ssabab.back.entity.*;
import ssabab.back.repository.AccountRepository;
import ssabab.back.repository.FoodRepository;
import ssabab.back.repository.MenuRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 메뉴 조회 및 등록/수정 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final AccountRepository accountRepository;

    /**
     * 특정 날짜에 해당하는 메뉴 2개를 조회하여 DTO로 반환
     */
    @Transactional(readOnly = true)
    public List<MenuResponseDTO> getMenusByDate(LocalDate date) {
        List<Menu> menus = menuRepository.findByDate(date);
        if (menus.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜의 메뉴가 존재하지 않습니다: " + date);
        }

        return menus.stream()
                .map(MenuResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 메뉴 등록 - 관리자만 가능
     */
    @Transactional
    public void saveMenu(MenuRequestDTO request) {
        Account user = getLoginUser();
        validateAdmin(user);

        List<Food> savedFoods = request.getFoods().stream()
                .map(f -> Food.builder()
                        .foodName(f.getFoodName())
                        .mainSub(f.getMainSub())
                        .category(f.getCategory())
                        .tag(f.getTag())
                        .build())
                .map(foodRepository::save)
                .collect(Collectors.toList());

        Menu menu = Menu.builder()
                .date(request.getDate())
                .foods(savedFoods)
                .build();

        menuRepository.save(menu);
    }

    /**
     * 메뉴 수정 - 관리자만 가능
     */
    @Transactional
    public void updateMenu(Long menuId, MenuRequestDTO request) {
        Account user = getLoginUser();
        validateAdmin(user);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));

        List<Food> savedFoods = request.getFoods().stream()
                .map(f -> Food.builder()
                        .foodName(f.getFoodName())
                        .mainSub(f.getMainSub())
                        .category(f.getCategory())
                        .tag(f.getTag())
                        .build())
                .map(foodRepository::save)
                .collect(Collectors.toList());

        menu.setDate(request.getDate());
        menu.setFoods(savedFoods);
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
