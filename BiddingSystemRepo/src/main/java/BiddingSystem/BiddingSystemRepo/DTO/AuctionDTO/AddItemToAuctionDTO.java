package BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO;

import BiddingSystem.BiddingSystemRepo.customAnotations.PresentOrFutureWithTolerance;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.apache.bcel.generic.RET;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

@Getter
@Setter
public class AddItemToAuctionDTO {

    private Long itemId;

    @PresentOrFutureWithTolerance
    private ZonedDateTime startingAt;

    private Duration auctionDuration;

    @Positive(message = "Reserve price must be positive number!")
    private BigDecimal reservePrice;

    @Positive(message = "Starting price must be positive number!")
    private BigDecimal startingPrice;

    @AssertTrue(message = "Auction duration must be between 10 minutes and 7 days")
    public boolean isAuctionDurationValid() {
        if (auctionDuration == null) {
            return true;
        }
        return auctionDuration.compareTo(Duration.ofMinutes(10)) >= 0
                && auctionDuration.compareTo(Duration.ofDays(7)) <= 0;
    }

    @AssertTrue
    public boolean isStartingPriceLessThanAuction(){
        if (reservePrice == null || startingPrice == null){
            return true;
        }
        return reservePrice.compareTo(startingPrice) > 0;
    }
}
