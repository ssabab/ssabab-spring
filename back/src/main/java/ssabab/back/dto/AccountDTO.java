package ssabab.back.dto;

import ssabab.back.entity.Account;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDTO {
    private Integer userId;
    private String email;
    private String password;
    private String username;

    public static AccountDTO fromEntity(Account acc) {
        return new AccountDTO(acc.getUserId(), acc.getEmail(),
                acc.getPassword(), acc.getUsername());
    }
}
