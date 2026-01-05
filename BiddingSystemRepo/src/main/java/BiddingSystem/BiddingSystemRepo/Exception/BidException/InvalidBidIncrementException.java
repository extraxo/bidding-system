package BiddingSystem.BiddingSystemRepo.Exception.BidException;


import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidBidIncrementException extends BaseCustomException {
    public InvalidBidIncrementException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}

