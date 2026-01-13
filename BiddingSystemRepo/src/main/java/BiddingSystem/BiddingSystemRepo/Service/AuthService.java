package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.UserLoginDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.UserRegisterDTO;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.EmailAlreadyExistsException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.InvalidPasswordException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UsernameAlreadyExistsException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.config.JwtGeneratorInterfaceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final PasswordEncoder encoder;
    private final JwtGeneratorInterfaceImpl jwtGeneratorInterface;
    private final UserRepository userRepository;


    public AuthService(UserRepository userRepository, JwtGeneratorInterfaceImpl jwtGeneratorInterface, PasswordEncoder encoder) {
        this.jwtGeneratorInterface = jwtGeneratorInterface;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public Map<String, String> login(UserLoginDTO dto) {

        User user = userRepository.findUserByEmail(dto.getEmail()).orElseThrow(() -> new UserNotFoundException("User with such email not found!"));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password!");
        }

        return jwtGeneratorInterface.generateToken(user);
    }


    public User register(UserRegisterDTO dto) {

        userRepository.findUserByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("User with this email already exists!");
                });

        userRepository.findUserByUsername(dto.getUsername())
                .ifPresent(user -> {
                    throw new UsernameAlreadyExistsException("User with such username already exists!");
                });

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setAddress(dto.getAddress());


        return userRepository.save(user);
    }


}
