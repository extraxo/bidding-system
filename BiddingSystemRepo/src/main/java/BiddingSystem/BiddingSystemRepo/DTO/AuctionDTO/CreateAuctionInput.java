package BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    @NotNull
    private final BigDecimal startingPrice;
    @Positive
    @NotNull
    private final BigDecimal reservePrice;

    @Positive
    @NotNull
    private final BigDecimal minimumIncrement;
}
