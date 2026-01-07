package BiddingSystem.BiddingSystemRepo.DTO.BidDTO;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class CreateBidDTO {

    Long auctionId;

    @Positive(message = "Value of new bid must be positive number!")
    BigDecimal bidPrice;

//    @PresentOrFutureWithTolerance(message = "Bid cannot be sent from the past!")
    ZonedDateTime sentDateTime;

}
