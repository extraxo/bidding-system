package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.UserLoginDTO;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.config.JwtGeneratorInterfaceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtGeneratorInterfaceImpl jwtGeneratorInterface;


    public AuthService(UserService userService,
                       JwtGeneratorInterfaceImpl jwtGeneratorInterface) {
        this.userService = userService;
        this.jwtGeneratorInterface = jwtGeneratorInterface;
    }

    public Map<String, String> login(UserLoginDTO dto) {
        User user = userService.getUserByEmail(dto.getEmail());

        if (user == null) {
            throw new UserNotFoundException("User");
        }

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalStateException("INVALID_PASSWORD");
        }

        return jwtGeneratorInterface.generateToken(user);
    }


}
