package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.UserLoginDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserRegisterDTO;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.EmailAlreadyExistsException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UsernameAlreadyExistsException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.config.JwtGeneratorInterfaceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService  {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtGeneratorInterfaceImpl jwtGeneratorInterface;


    public UserService(UserRepository userRepository,JwtGeneratorInterfaceImpl jwtGeneratorInterface){
        this.userRepository = userRepository;
        this.jwtGeneratorInterface = jwtGeneratorInterface;
    }

    public User register(UserRegisterDTO dto) {
        if (userRepository.findUserByEmail(dto.getEmail()) != null) {
            throw new EmailAlreadyExistsException("EMAIL_ALREADY_EXISTS");
        }

        if (userRepository.findUserByUsername(dto.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("USERNAME_ALREADY_EXISTS");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setPassword(encoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }


    public User getUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

}
