package ssabab.back.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    private Integer foodScore;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
