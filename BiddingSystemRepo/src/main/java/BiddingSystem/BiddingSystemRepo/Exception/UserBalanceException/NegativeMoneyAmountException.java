package BiddingSystem.BiddingSystemRepo.Exception.UserBalanceException;


import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NegativeMoneyAmountException extends BaseCustomException {
    public NegativeMoneyAmountException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}