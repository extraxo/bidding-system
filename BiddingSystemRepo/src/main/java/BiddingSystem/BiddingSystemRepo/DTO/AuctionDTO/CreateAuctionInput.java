package BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class CreateAuctionInput {

    private final Long itemId;
    private final ZonedDateTime startingAt;
    private final Duration duration;
    private final BigDecimal startingPrice;
    private final BigDecimal reservePrice;
}
