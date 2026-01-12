package BiddingSystem.BiddingSystemRepo.Exception.SystemBalanceExceptions;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class SystemNotFoundException extends BaseCustomException {

    public SystemNotFoundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}