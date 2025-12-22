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

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public User getUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

}
