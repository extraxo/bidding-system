package BiddingSystem.BiddingSystemRepo.Exception.AuctionException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserInsufficientBalanceException extends BaseCustomException {
    public UserInsufficientBalanceException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}