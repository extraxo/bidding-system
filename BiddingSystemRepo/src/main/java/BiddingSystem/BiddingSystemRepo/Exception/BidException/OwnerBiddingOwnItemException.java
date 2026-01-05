package BiddingSystem.BiddingSystemRepo.Exception.BidException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class OwnerBiddingOwnItemException extends BaseCustomException {
    public OwnerBiddingOwnItemException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}