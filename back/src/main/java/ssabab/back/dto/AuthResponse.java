package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    // Refresh Token은 클라이언트에 보내지 않으므로 제외
    private String email;
    private String username;
}
