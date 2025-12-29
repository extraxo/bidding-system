package BiddingSystem.BiddingSystemRepo.Exception.AuctionException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ItemAlreadyInAuction extends BaseCustomException {
    public ItemAlreadyInAuction(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}