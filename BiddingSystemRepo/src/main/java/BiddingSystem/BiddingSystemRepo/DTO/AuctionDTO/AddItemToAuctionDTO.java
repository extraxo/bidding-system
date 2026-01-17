package BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddItemToAuctionDTO {

    private Long itemId;

    private ZonedDateTime startingAt;

    @NotNull(message = "Duration can not be empty!")
    private Duration auctionDuration;

    @Positive(message = "Reserve price must be positive number!")
    private BigDecimal reservePrice;

    @Positive(message = "Starting price must be positive number!")
    private BigDecimal startingPrice;

    @Positive(message = "Minimal increment must be positive number!")
    private BigDecimal minimumIncrement;

    @JsonIgnore
    @AssertTrue(message = "Auction duration must be between 10 minutes and 7 days")
    public boolean isAuctionDurationValid() {
        if (auctionDuration == null) {
            return true;
        }
        return auctionDuration.compareTo(Duration.ofMinutes(10)) >= 0
                && auctionDuration.compareTo(Duration.ofDays(7)) <= 0;
    }

    @JsonIgnore
    @AssertTrue(message = "Starting price must be less than reserve one.")
    private boolean isStartingPriceLessThanAuction(){
        if (reservePrice == null || startingPrice == null){
            return true;
        }
        return reservePrice.compareTo(startingPrice) > 0;
    }
}
