package BiddingSystem.BiddingSystemRepo.Exception.UserExceptions;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BaseCustomException{

    public InvalidPasswordException(String message){
        super(message, HttpStatus.BAD_REQUEST);
    }
}
