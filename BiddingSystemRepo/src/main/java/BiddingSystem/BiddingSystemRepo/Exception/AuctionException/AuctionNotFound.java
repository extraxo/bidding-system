package BiddingSystem.BiddingSystemRepo.Exception.AuctionException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AuctionNotFound extends BaseCustomException {
    public AuctionNotFound(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}

