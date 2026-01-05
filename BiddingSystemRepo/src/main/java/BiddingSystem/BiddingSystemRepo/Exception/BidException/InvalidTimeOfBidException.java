package BiddingSystem.BiddingSystemRepo.Exception.BidException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidTimeOfBidException extends BaseCustomException {
    public InvalidTimeOfBidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}

