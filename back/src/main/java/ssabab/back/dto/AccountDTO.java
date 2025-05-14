package ssabab.back.dto;

import ssabab.back.entity.Account;
import lombok.*;


@Data
public class AccountDTO {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    private String password;
    
    @NotBlank(message = "이름(별칭)을 입력해주세요.")
    private String username;
    
    // 기타 가입 폼 필드가 있다면 추가 (예: 전화번호 등). 로그인시에는 email/password만 사용.
}



// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @ToString
// public class AccountDTO {
//     private Integer userId;
//     private String email;
//     private String password;
//     private String username;

//     public static AccountDTO fromEntity(Account acc) {
//         return new AccountDTO(acc.getUserId(), acc.getEmail(),
//                 acc.getPassword(), acc.getUsername());
//     }
// }
