package ssabab.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssabab.back.dto.MenuResponseDTO;
import ssabab.back.service.MenuService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 메뉴 조회 관련 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    /**
     * 특정 날짜의 메뉴 2개 조회
     * 요청 예시: GET /api/menu?date=2025-06-13
     */
    @GetMapping
    public ResponseEntity<Object> getMenusByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<MenuResponseDTO> menus = menuService.getMenusByDate(date);
            return ResponseEntity.ok(Map.of("menus", menus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
