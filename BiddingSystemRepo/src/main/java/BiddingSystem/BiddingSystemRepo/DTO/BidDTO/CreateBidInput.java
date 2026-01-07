package BiddingSystem.BiddingSystemRepo.DTO.BidDTO;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class CreateBidInput {

    Long auctionId;
    BigDecimal bidPrice;
}
