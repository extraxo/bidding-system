package BiddingSystem.BiddingSystemRepo.Exception.UserExceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends BaseCustomException{

    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);

    }

}
