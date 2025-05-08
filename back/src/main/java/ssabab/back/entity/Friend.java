package ssabab.back.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(FriendId.class)
public class Friend {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Account user;

    @Id
    @ManyToOne
    @JoinColumn(name = "friend_id")
    private Account friend;
}