package BiddingSystem.BiddingSystemRepo.Exception.UserBalanceException;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class MaxDepositWithdrawAmountException extends BaseCustomException {
    public MaxDepositWithdrawAmountException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
