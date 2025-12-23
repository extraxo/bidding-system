package BiddingSystem.BiddingSystemRepo.Exception;


import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.BaseCustomException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.InvalidPasswordException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseCustomException.class)
    public ProblemDetail handleBaseCustomException(BaseCustomException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(ex.getStatus());
        pd.setTitle(ex.getClass().getSimpleName());
        pd.setDetail(ex.getMessage());
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("timestamp", LocalDateTime.now().toString());

        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        pd.setTitle("Validation Failed");
        pd.setDetail("One or more fields are invalid");
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("timestamp", LocalDateTime.now().toString());

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(err ->
                        fieldErrors.put(err.getField(), err.getDefaultMessage())
                );

        pd.setProperty("errors", fieldErrors);

        return pd;
    }



}
