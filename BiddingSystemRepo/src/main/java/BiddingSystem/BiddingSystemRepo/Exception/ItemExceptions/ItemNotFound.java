package BiddingSystem.BiddingSystemRepo.Exception.ItemExceptions;

import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ItemNotFound extends BaseCustomException {
    public ItemNotFound(String message) {
        super(message, HttpStatus.BAD_REQUEST);

    }
}
