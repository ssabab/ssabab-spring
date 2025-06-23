// dto.TokenDTO
package ssabab.back.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * JWT 토큰 응답 DTO
 */
@Getter
@Builder
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn; // Access Token 만료 시간 (초)
}