// dto.AccountDTO
package ssabab.back.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 회원 프로필 정보 DTO (응답용)
 */
@Data
public class AccountDTO {
    private Long userId;
    private String username;
    private String email;
    private String ssafyYear;
    private String classNum;
    private String ssafyRegion;
    private String gender;
    private LocalDate birthDate;
}

