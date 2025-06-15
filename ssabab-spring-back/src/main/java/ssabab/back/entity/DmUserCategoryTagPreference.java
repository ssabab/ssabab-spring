package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dm_user_category_tag_preference")
public class DmUserCategoryTagPreference {
    @Id
    @Column(name = "user_id") // user_id가 PK
    private Long userId;

    @Column(name = "tag_json", columnDefinition = "LONGTEXT") // longtext 타입으로 매핑
    private String tagJson;

    @Column(name = "category_json", columnDefinition = "LONGTEXT") // longtext 타입으로 매핑
    private String categoryJson;
}