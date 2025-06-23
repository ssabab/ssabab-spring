package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.*;
import ssabab.back.service.MenuService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    // 1. 일간 메뉴 조회
    @GetMapping
    public ResponseEntity<Object> getDailyMenus(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Map<String, Object>> menus = menuService.getMenusByDate(date);
            if (menus.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "해당 날짜에는 메뉴가 없습니다", "date", date.toString()));
            }
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // 2. 주간 메뉴 조회
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyMenuResponse> getWeeklyMenus() {
        try {
            WeeklyMenuResponse response = menuService.getWeeklyMenus();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 3. 관리자 - 일간 메뉴 등록
    @PostMapping("/{date}")
    public ResponseEntity<Object> createDailyMenus(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody List<MenuBlockRequest> request) {
        try {
            menuService.saveDailyMenus(date, request);
            return ResponseEntity.status(HttpStatus.CREATED).body("메뉴가 등록되었습니다");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 4. 관리자 - 단일 메뉴 수정
    @PutMapping("/{menuId}")
    public ResponseEntity<Object> updateMenu(
            @PathVariable Long menuId,
            @RequestBody List<MenuRequestDTO.FoodRequestDTO> request) {
        try {
            menuService.updateMenu(menuId, request);
            return ResponseEntity.ok("메뉴가 수정되었습니다");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 5. 관리자 - 일간 메뉴 전체 삭제
    @DeleteMapping("/{date}")
    public ResponseEntity<Object> deleteDailyMenus(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            menuService.deleteDailyMenus(date);
            return ResponseEntity.ok("메뉴가 삭제되었습니다");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
