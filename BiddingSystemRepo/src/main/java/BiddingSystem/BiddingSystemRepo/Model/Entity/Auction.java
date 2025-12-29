package BiddingSystem.BiddingSystemRepo.Model.Entity;


import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "auction")
public class Auction extends BaseEntity {

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User owner;

    @ManyToOne
    private Item item;

    private LocalDateTime listedAt = LocalDateTime.now();

    private LocalDateTime startingAt;

    private Duration auctionDuration = Duration.ofDays(1);

    private LocalDateTime endsAt;

    private BigDecimal reservePrice;

//    Active auctions will be set in cache - fast-access memory store -> Write-through, Write-behind (Lazy), Cache-Aside
    @Enumerated(value = EnumType.STRING)
    private AuctionStatusEnum auctionStatusEnum;

    @ManyToOne
    private User winner;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    private List<Bid> winnerBid;


}
