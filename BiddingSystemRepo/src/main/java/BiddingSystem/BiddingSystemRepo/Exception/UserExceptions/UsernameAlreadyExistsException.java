package BiddingSystem.BiddingSystemRepo.Exception.UserExceptions;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BaseCustomException {
    public UsernameAlreadyExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
