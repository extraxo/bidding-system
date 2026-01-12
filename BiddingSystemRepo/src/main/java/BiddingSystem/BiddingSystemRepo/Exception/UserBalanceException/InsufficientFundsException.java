package BiddingSystem.BiddingSystemRepo.Exception.UserBalanceException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends BaseCustomException {
    public InsufficientFundsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}