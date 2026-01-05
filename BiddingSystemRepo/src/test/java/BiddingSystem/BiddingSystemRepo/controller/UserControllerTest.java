package BiddingSystem.BiddingSystemRepo.controller;


import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.UserRegisterDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

// Test for invalid input

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private Validator validator;

    @Test
    void givenInvalidAge_shouldFailValidation() {
        UserRegisterDTO dto = new UserRegisterDTO(
                "coolUsername",
                12,
                "emailBg@abv.bg",
                "thePassword6587*",
                "Grove Street"
        );

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void givenInvalidEmail_shouldFailValidation() {
        UserRegisterDTO dto = new UserRegisterDTO(
                "GoodUser123",
                12,
                "someEmail.bg",
                "thePassword6587*",
                "Grove Street"
        );

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

}
