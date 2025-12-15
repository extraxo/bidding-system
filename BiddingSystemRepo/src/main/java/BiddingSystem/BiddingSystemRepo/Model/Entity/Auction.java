package BiddingSystem.BiddingSystemRepo.Model.Entity;


import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "auction")
public class Auction extends BaseEntity {


    @ManyToOne(cascade = CascadeType.PERSIST)
    private User owner;

    @OneToOne
    private Item item;

    private LocalDateTime created_at = LocalDateTime.now();

    private LocalDateTime expire_at;


//    Active auctions will be set in cache - fast-access memory store -> Write-through, Write-behind (Lazy), Cache-Aside
    @Enumerated(value = EnumType.STRING)
    private AuctionStatusEnum auctionStatusEnum;

    @ManyToOne
    private User winner;

    //    current One
    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    private List<Bid> winnerBid;

//    private BigDecimal winnerPrice - > winnerBid.getPrice();

}
