package BiddingSystem.BiddingSystemRepo.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bid")
public class Bid extends BaseEntity {

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Auction auction;

    private BigDecimal price;

    @OneToOne
    private User user;

    private LocalDateTime created_at = LocalDateTime.now();

}
