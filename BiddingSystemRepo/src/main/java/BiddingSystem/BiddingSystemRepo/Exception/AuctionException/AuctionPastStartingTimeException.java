package BiddingSystem.BiddingSystemRepo.Exception.AuctionException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AuctionPastStartingTimeException extends BaseCustomException {
    public AuctionPastStartingTimeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}