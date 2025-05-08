package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ssabab.back.dto.AccountDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Account {

    /* ---------- PK 타입 Long 으로 통일 ---------- */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false)               private String password;
    @Column(nullable = false)               private String username;

    // 소셜 확장용
    private String provider;
    private String providerId;

    private String profileImgUrl = "default";
    private String role          = "USER";
    private String refreshToken;
    private Boolean active       = true;

    @CreationTimestamp @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /* ---------- DTO → Entity ---------- */
    public static Account fromDTO(AccountDTO dto) {
        return Account.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .build();
    }
}
