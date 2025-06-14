package ssabab.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssabab.back.dto.MenuResponseDTO;
import ssabab.back.entity.Menu;
import ssabab.back.repository.MenuRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 메뉴 조회 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

	/**
	 * 특정 날짜에 해당하는 메뉴 2개를 조회하여 DTO로 반환
	 */
	@Transactional(readOnly = true)
	public List<MenuResponseDTO> getMenusByDate(LocalDate date) {
		List<Menu> menus = menuRepository.findByDate(date);
		if (menus.isEmpty()) {
			throw new IllegalArgumentException("해당 날짜의 메뉴가 존재하지 않습니다.: " + date);
		}
		
		return menus.stream()
				.map(MenuResponseDTO::from)
				.collect(Collectors.toList());
	}
}