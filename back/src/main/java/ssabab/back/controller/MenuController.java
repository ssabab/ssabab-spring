package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.MenuWithFoodsDTO;
import ssabab.back.service.MenuService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/{date}")
    public ResponseEntity<List<MenuWithFoodsDTO>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(menuService.getMenusByDate(date));
    }

    @PostMapping("/{date}")
    public ResponseEntity<Void> createMenus(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody List<MenuWithFoodsDTO> menuDTOs) {
        menuService.createOrUpdateMenus(date, menuDTOs, false);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{date}")
    public ResponseEntity<Void> updateMenus(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody List<MenuWithFoodsDTO> menuDTOs) {
        menuService.createOrUpdateMenus(date, menuDTOs, true);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{date}")
    public ResponseEntity<Void> deleteMenus(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        menuService.deleteMenusByDate(date);
        return ResponseEntity.ok().build();
    }
}
