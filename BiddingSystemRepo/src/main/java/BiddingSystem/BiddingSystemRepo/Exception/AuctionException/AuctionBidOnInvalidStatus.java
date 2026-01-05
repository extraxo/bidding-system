package BiddingSystem.BiddingSystemRepo.Exception.AuctionException;


import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AuctionBidOnInvalidStatus extends BaseCustomException {
    public AuctionBidOnInvalidStatus(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}

