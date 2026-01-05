package BiddingSystem.BiddingSystemRepo.Model.Entity;


import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "auction")
public class Auction extends BaseEntity {

    @ManyToOne
    private Item item;

    private ZonedDateTime listedAt = ZonedDateTime.now();

    private ZonedDateTime startingAt = ZonedDateTime.now();

    private Duration auctionDuration;

    private ZonedDateTime endsAt;

    private BigDecimal reservePrice;

    private BigDecimal startingPrice;

    private BigDecimal minimumIncrement;

    @Enumerated(value = EnumType.STRING)
    private AuctionStatusEnum auctionStatusEnum;

    private ZonedDateTime paymentDeadline;

    @ManyToOne
    private User winner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_bid_id")
    private Bid winnerBid;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    private List<Bid> listOfBids;

    @PrePersist
    public void setEndsAt(){
        this.endsAt = this.startingAt.plus(auctionDuration);
    }

}
