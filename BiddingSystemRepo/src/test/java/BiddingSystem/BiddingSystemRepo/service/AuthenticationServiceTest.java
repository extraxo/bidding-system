package BiddingSystem.BiddingSystemRepo.service;

import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.UserLoginDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.UserRegisterDTO;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.EmailAlreadyExistsException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.InvalidPasswordException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UsernameAlreadyExistsException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.Service.AuthService;
import BiddingSystem.BiddingSystemRepo.config.JwtGeneratorInterfaceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// TODO: Add logout tests
// AAA - Arrange, Act, Assert
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtGeneratorInterfaceImpl jwtGenerator;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;


    @Test
    public void givenCredentials_shouldLogin() {

        UserLoginDTO dto = new UserLoginDTO(
                "kacoLudiq@abv.bg",
                "ivoIstinata"
        );

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        Map<String,String> fakeToken = Map.of("token","jwt-token-123");

        when(userRepository.findUserByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword()
        )).thenReturn(true);

        when(jwtGenerator.generateToken(user))
                .thenReturn(fakeToken);

        Map<String, String> result = authService.login(dto);

        assertNotNull(result);
        assertEquals("jwt-token-123", result.get("token"));
    }

    @Test
    public void givenInvalidEmail_shouldFailLogin(){


        UserLoginDTO loginDTO = new UserLoginDTO(
                "unknown@example.com",
                "SecPass123+"
        );

        when(userRepository.findUserByEmail(loginDTO.getEmail()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.login(loginDTO));
        assertEquals("User with such email not found!", exception.getMessage());
    }

    @Test
    public void givenInvalidPassword_shouldFailLogin(){
        UserLoginDTO dto = new UserLoginDTO(
                "known123@example.com",
                "GoodPass123+"
        );

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        when(userRepository.findUserByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword()
        )).thenReturn(false);

        InvalidPasswordException invalidPasswordException = assertThrows(
                InvalidPasswordException.class,
                () -> authService.login(dto)
        );
        assertEquals("Invalid password!", invalidPasswordException.getMessage());

    }

    @Test
    public void givenRightInput_shouldRegisterSuccessfully(){
        UserRegisterDTO dto = new UserRegisterDTO(
                "uniqueUsername123",
                20,
                "newEmail@abv.bg",
                "thePas34+",
                "Grove Street"
        );

        String encodedPass = "ENCODED_PASSWORD";

        when(userRepository.findUserByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());

        when(userRepository.findUserByUsername(dto.getUsername()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(dto.getPassword()))
                .thenReturn(encodedPass);

        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        User result = authService.register(dto);

        assertNotNull(result);
        assertEquals(dto.getUsername(), result.getUsername());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getAge(), result.getAge());
        assertEquals(encodedPass, result.getPassword());

    }

    @Test
    public void givenUserWithRepetitiveEmail_shouldRegisterFail(){
        UserRegisterDTO dto = new UserRegisterDTO(
                "theUser",
                30,
                "repetitiEmail@abv.bg",
                "SecretPassword123",
                "Grove Street"
        );

        User repetitiveUser = new User();
        repetitiveUser.setEmail("repetitiEmail@abv.bg");

        when(userRepository.findUserByEmail(dto.getEmail()))
                .thenReturn(Optional.of(repetitiveUser));

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(dto));

        assertEquals("User with this email already exists!", exception.getMessage());

    }

    @Test
    public void givenUserWithRepetitiveUsername_shouldRegisterFail(){
        UserRegisterDTO dto = new UserRegisterDTO(
                "repetitiveUser",
                30,
                "unique@abv.bg",
                "SecretPassword123",
                "Grove Street"
        );

        User repetitiveUser = new User();
        repetitiveUser.setUsername("repetitiveUser");

        when(userRepository.findUserByUsername(dto.getUsername()))
                .thenReturn(Optional.of(repetitiveUser));

        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class,
                () -> authService.register(dto));

        assertEquals("User with such username already exists!", exception.getMessage());

    }




}
