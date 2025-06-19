package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.DailyMenuResponse;
import ssabab.back.dto.MenuBlockRequest; // MenuBlockRequest 임포트
import ssabab.back.dto.MenuRequestDTO;
import ssabab.back.dto.MenuResponseDTO;
import ssabab.back.dto.WeeklyMenuResponse;
import ssabab.back.service.MenuService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 메뉴 조회 및 등록/수정 관련 API 컨트롤러
 * (menuOrder 필드 없음 - menu_id 순서로 menu1/menu2 구분, 불안정)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    /**
     * 특정 날짜의 메뉴 2개 (menu1, menu2) 조회
     */
    @GetMapping
    public ResponseEntity<Object> getDailyMenus(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            DailyMenuResponse dailyMenu = menuService.getMenusByDate(date);
            return ResponseEntity.ok(dailyMenu);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 지난주 월요일부터 이번 주 금요일까지의 주간 메뉴 데이터를 조회합니다.
     */
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyMenuResponse> getWeeklyMenus() {
        try {
            WeeklyMenuResponse weeklyMenus = menuService.getWeeklyMenus();
            return ResponseEntity.ok(weeklyMenus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 관리자 메뉴 등록 (하루 2개 메뉴를 동시에 등록)
     * POST /api/menu/{date} 로 경로 변경, RequestBody는 List<MenuBlockRequest>
     */
    @PostMapping("/{date}")
    public ResponseEntity<Object> createDailyMenus(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody List<MenuBlockRequest> requestBody) {
        try {
            menuService.saveDailyMenus(date, requestBody);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "메뉴가 등록되었습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

//    @GetMapping("/{date")
//
    /**
     * 관리자 메뉴 수정
     * 요청 예시: PUT /api/menu/{menuId}
     * RequestBody: { "date": "2025-06-16", "foods": [...] }
     */
    @PutMapping("/{menuId}")
    public ResponseEntity<Object> updateMenu(
            @PathVariable Long menuId,
            @RequestBody MenuRequestDTO request) {
        try {
            menuService.updateMenu(menuId, request);
            return ResponseEntity.ok(Map.of("message", "메뉴가 수정되었습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}