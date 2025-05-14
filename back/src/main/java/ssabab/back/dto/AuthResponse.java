package ssabab.back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for authentication responses (JWT token and basic user info).
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String email;
    private String username;
}
