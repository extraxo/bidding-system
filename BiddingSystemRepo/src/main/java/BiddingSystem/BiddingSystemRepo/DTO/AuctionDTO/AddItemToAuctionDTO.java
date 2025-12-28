package BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddItemToAuctionDTO {

    Long itemId;

    boolean isDraft;
}
