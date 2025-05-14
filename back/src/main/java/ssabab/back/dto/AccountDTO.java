package ssabab.back.dto;

import ssabab.back.entity.Account;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDTO {
    private INTGER userId;
    private String email;
    private String password;
    private String username;

    public static AccountDTO toAccountDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setUserId(account.getUserId());
        dto.setEmail(account.getEmail());
        dto.setUsername(account.getUsername());
        dto.setPassword(null);  // Do not include password in DTO output
        return dto;
    }
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
