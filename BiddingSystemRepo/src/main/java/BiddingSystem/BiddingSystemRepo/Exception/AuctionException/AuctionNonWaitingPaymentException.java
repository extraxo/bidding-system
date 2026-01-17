package BiddingSystem.BiddingSystemRepo.Exception.AuctionException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AuctionNonWaitingPaymentException extends BaseCustomException {
    public AuctionNonWaitingPaymentException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }



}
