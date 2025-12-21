package BiddingSystem.BiddingSystemRepo.Exception.UserExceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseCustomException extends RuntimeException {
    private final HttpStatus status;

    public BaseCustomException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    public BaseCustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
