package ssabab.back.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 회원가입 요청 DTO (추가 정보 입력용)
 */
@Data
public class SignupRequestDTO {
    private String username;
    private String ssafyYear;
    private String classNum;
    private String ssafyRegion;
    private String gender;
    private LocalDate birthDate;
}
