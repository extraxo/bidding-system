package BiddingSystem.BiddingSystemRepo.Exception.UserExceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BaseCustomException {
    public EmailAlreadyExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}