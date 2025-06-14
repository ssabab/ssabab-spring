package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

import lombok.Builder;
import lombok.AllArgsConstructor;

/**
 * 회원 계정 엔티티 - 소셜 로그인 계정 및 추가 프로필 정보를 저장
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(length = 255)
    private String password;         // 비밀번호 (소셜 로그인 사용 시 null 처리)

    @Column(name = "provider", length = 20)
    private String provider;         // 소셜 로그인 제공자 (예: "google")

    @Column(name = "provider_id", length = 100)
    private String providerId;       // 제공자 측 사용자 고유 ID

    @Column(name = "username", length = 50)
    private String username;         // 닉네임

    @Column(name = "email", length = 100)
    private String email;            // 이메일

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 계정 생성시각

    @Column(name = "active")
    private boolean active;          // 활성 계정 여부

    @Column(name = "profile_image_url")
    private String profileImageUrl; // 프로필 이미지 URL

    @Column(name = "role", length = 20)
    private String role;             // 권한(Role) - 예: "USER"


    @Column(name = "refresh_token",length = 2048)
    private String refreshToken;    // (선택) OAuth2 Refresh Token 저장용

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 프로필 정보 마지막 수정시각

    @Column(name = "ssafy_year", length = 4)
    private String ssafyYear;        // SSAFY 기수 (연도 혹은 회차)

    @Column(name = "class_num", length = 10)
    private String classNum;         // SSAFY 반 (클래스 번호)

    @Column(name = "ssafy_region", length = 20)
    private String ssafyRegion;      // SSAFY 교육 지역 (예: 서울, 대전 등)

    @Column(name = "gender", length = 10)
    private String gender;           // 성별

    @Column(name = "birth_date")
    private LocalDate birthDate;             // 태어난 연도(나이와 관계된 값)
}