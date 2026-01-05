package BiddingSystem.BiddingSystemRepo.Exception.BidException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class BidSentAfterEndTimeException extends BaseCustomException {
    public BidSentAfterEndTimeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}
