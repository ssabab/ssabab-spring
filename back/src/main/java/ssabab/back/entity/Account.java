package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ssabab.back.dto.AccountDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@Setter  // Added to allow updating fields (password, refreshToken, etc.)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private INTGER userId;

    @Column(nullable = false)
    private String password;

    @Column
    private String provider;  // e.g. "LOCAL", "GOOGLE", "GITHUB"

    @Column(name = "provider_id")
    private String providerId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image_url")
    private String profileImgUrl;

    @Column
    private String role;

    @Column(name = "refresh_token")
    private String refreshToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column
    private Boolean active;
}




// @Entity
// @Table(name = "account")
// @Getter @Setter
// @NoArgsConstructor @AllArgsConstructor @Builder
// public class Account {

//     /* ---------- PK 타입 Long 으로 통일 ---------- */
//     @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer userId;

//     @Column(nullable = false, unique = true) private String email;
//     @Column(nullable = false)               private String password;
//     @Column(nullable = false)               private String username;

//     // 소셜 확장용
//     private String provider;
//     private String providerId;

//     private String profileImgUrl = "default";
//     private String role          = "USER";
//     private String refreshToken;
//     private Boolean active       = true;

//     @CreationTimestamp @Column(updatable = false)
//     private LocalDateTime createdAt;
//     @UpdateTimestamp
//     private LocalDateTime updatedAt;

//     /* ---------- DTO → Entity ---------- */
//     public static Account fromDTO(AccountDTO dto) {
//         return Account.builder()
//                 .email(dto.getEmail())
//                 .password(dto.getPassword())
//                 .username(dto.getUsername())
//                 .build();
//     }
// }
