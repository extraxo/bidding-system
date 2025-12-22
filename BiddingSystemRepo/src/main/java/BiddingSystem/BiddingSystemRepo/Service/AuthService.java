package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.UserLoginDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserRegisterDTO;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.EmailAlreadyExistsException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.InvalidPasswordException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UsernameAlreadyExistsException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.config.JwtGeneratorInterfaceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtGeneratorInterfaceImpl jwtGeneratorInterface;
    private final UserRepository userRepository;


    public AuthService(UserRepository userRepository, JwtGeneratorInterfaceImpl jwtGeneratorInterface) {
        this.jwtGeneratorInterface = jwtGeneratorInterface;
        this.userRepository = userRepository;
    }

    public Map<String, String> login(UserLoginDTO dto) {
        User user = userRepository.findUserByEmail(dto.getEmail());

        if (user == null) {
            throw new UserNotFoundException("User not registered yet!");
        }

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password!");
        }

        return jwtGeneratorInterface.generateToken(user);
    }


    public User register(UserRegisterDTO dto) {
        if (userRepository.findUserByEmail(dto.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        if (userRepository.findUserByUsername(dto.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setPassword(encoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }


}
