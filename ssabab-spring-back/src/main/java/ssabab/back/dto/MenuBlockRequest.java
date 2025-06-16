package ssabab.back.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * POST /api/menu/{date} 요청 본문의 각 메뉴 블록을 나타내는 DTO
 * foods 리스트만 가집니다.
 */
@Getter
@Setter
public class MenuBlockRequest {
    private List<MenuRequestDTO.FoodRequestDTO> foods;
}