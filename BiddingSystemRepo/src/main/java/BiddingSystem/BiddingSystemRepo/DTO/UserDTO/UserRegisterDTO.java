package BiddingSystem.BiddingSystemRepo.DTO.UserDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRegisterDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @Min(value = 18, message = "Your age must be at >= 18 years old")
    @Max(value = 80, message = "Your age must be at <= 80 years old")
    private int age;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Address is required")
    private String address;

}
