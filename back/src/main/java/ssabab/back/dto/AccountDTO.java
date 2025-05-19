package ssabab.back.dto;

import ssabab.back.entity.Account;
import lombok.*;
import lombok.Builder.Default;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccountDTO {
    private Integer id;
    private String email;
    private String password;
    private String username;
    @Default
    private String role = "0"; // 기본값 "0"
    private Integer ordNum;    // SSAFY 기수 (예: 12)
    private Integer classNum;
    private String ssafyRegion;
    private Integer gender;    // 성별 (0 또는 1)
    private Integer birthYear; // 출생년도

    public static AccountDTO fromEntity(Account acc) {
        AccountDTO dto = new AccountDTO();
        dto.setId(acc.getId());
        dto.setEmail(acc.getEmail());
        dto.setPassword(acc.getPassword());
        dto.setUsername(acc.getUsername());
        dto.setRole("0"); // 항상 "0"으로 설정
        dto.setOrdNum(acc.getOrdNum());
        dto.setClassNum(acc.getClassNum());
        dto.setSsafyRegion(acc.getSsafyRegion());
        dto.setGender(acc.getGender());
        dto.setBirthYear(acc.getBirthYear());
        return dto;
    }
}
