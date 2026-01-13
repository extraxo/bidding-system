package BiddingSystem.BiddingSystemRepo.Controller;


import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.UserLoginDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.UserRegisterDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserDTO.UserRegisterResponseDTO;
import BiddingSystem.BiddingSystemRepo.Service.AuthService;
import BiddingSystem.BiddingSystemRepo.config.BlacklistStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Map;


@Tag(
        name = "User account management"
)
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final SecretKey key;
    private final BlacklistStore blacklistStore;


    public UserController(AuthService authService,ModelMapper modelMapper,
                          SecretKey key, BlacklistStore blacklistStore) {
        this.authService = authService;
        this.modelMapper = modelMapper;
        this.key = key;
        this.blacklistStore = blacklistStore;
    }


    @Operation(
            summary = "Register a user"
    )
    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return ResponseEntity.ok(modelMapper.map(authService.register(dto), UserRegisterResponseDTO.class));
    }


    @Operation(
            summary = "Login user"
    )
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginDTO dto) {
//        TODO: When having token, unaccessible, not relevant error
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(
            summary = "Logout user and blacklist token"
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        String jti = (String) request.getAttribute("jti");

        if (jti == null) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        blacklistStore.addToken(jti);
        return ResponseEntity.ok("Logged out successfully");
    }
}

