package BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO;

import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.BidDTO;
import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.OutputItemDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.AuctionBidderDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.AuctionOwnerDTO;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class ExposeAuctionDTO {

    private Long id;

    private ZonedDateTime startingAt;

    private Duration auctionDuration;

    private ZonedDateTime endsAt;

    private BigDecimal startingPrice;

    private BigDecimal minimumIncrement;

//    Added now
    private BigDecimal highestBid;

    @Enumerated(value = EnumType.STRING)
    private AuctionStatusEnum auctionStatusEnum;

    private OutputItemDTO item;

    private AuctionBidderDTO winner;

    private AuctionOwnerDTO owner;

    private List<BidDTO> listOfBids;

}
