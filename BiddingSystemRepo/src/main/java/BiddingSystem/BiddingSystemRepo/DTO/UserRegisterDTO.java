package BiddingSystem.BiddingSystemRepo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class UserRegisterDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @Min(value = 18, message = "You must be at least 18 years old")
    private int age;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
