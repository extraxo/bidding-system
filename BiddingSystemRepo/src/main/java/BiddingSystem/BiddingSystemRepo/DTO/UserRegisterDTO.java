package BiddingSystem.BiddingSystemRepo.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class UserRegisterDTO {

    private String username;

    private int age;

    private String email;

    private String password;
}
