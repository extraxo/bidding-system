package BiddingSystem.BiddingSystemRepo.Exception.BidException;


import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidBidException extends BaseCustomException {
    public InvalidBidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}
