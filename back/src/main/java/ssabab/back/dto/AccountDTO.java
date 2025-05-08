package ssabab.back.dto;

import ssabab.back.entity.Account;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDTO {
    private Long userId;
    private String email;
    private String password;
    private String username;

    public static AccountDTO toAccountDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setuserId(account.getuserId());
        accountDTO.setAccountEmail(account.getAccountEmail());
        accountDTO.setAccountPassword(account.getAccountPassword());
        accountDTO.setAccountName(account.getAccountName());
        return accountDTO;
    }
}
