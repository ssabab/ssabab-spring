package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssabab.back.repository.MenuRepository;
import ssabab.back.entity.Menu;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Optional<Menu> getMenuById(Integer id) {
        return menuRepository.findById(id);
    }

    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu updateMenu(Integer id, Menu updatedMenu) {
        return menuRepository.findById(id)
                .map(menu -> {
                    menu.setDate(updatedMenu.getDate());
                    return menuRepository.save(menu);
                })
                .orElseThrow(() -> new RuntimeException("Menu not found"));
    }

    public void deleteMenu(Integer id) {
        menuRepository.deleteById(id);
    }
}
