package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssabab.back.dto.AccountDTO;

import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String provider;
    private String providerId;
    private String username;
    private String profileImageUrl;
    private String role;
    private String refreshToken;
    private Integer ordNum;     // SSAFY 기수 (예: 12)
    private Integer classNum;
    private String ssafyRegion;
    private Integer gender;     // 성별 (0 또는 1)
    private Integer birthYear;  // 출생년도 (예: 1998)
    
    public Integer getOrdNum() {
        return ordNum;
    }
    
    public Integer getBirthYear() {
        return birthYear;
    }
    
    public static Account fromDTO(AccountDTO dto) {
        return Account.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .nickname(dto.getUsername())
                .role("0")
                .ordNum(dto.getOrdNum())
                .classNum(dto.getClassNum())
                .ssafyRegion(dto.getSsafyRegion())
                .gender(dto.getGender())
                .birthYear(dto.getBirthYear())
                .build();
    }
}