package BiddingSystem.BiddingSystemRepo.Controller;


import BiddingSystem.BiddingSystemRepo.DTO.UserLoginDTO;
import BiddingSystem.BiddingSystemRepo.DTO.UserRegisterDTO;
import BiddingSystem.BiddingSystemRepo.Response.UserRegisterResponseDTO;
import BiddingSystem.BiddingSystemRepo.Service.AuthService;
import BiddingSystem.BiddingSystemRepo.Service.UserService;
import BiddingSystem.BiddingSystemRepo.config.BlacklistStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final ModelMapper modelMapper;
    private final SecretKey key;
    private final BlacklistStore blacklistStore;


    public UserController(UserService userService, AuthService authService,ModelMapper modelMapper,
                          SecretKey key, BlacklistStore blacklistStore) {
        this.userService = userService;
        this.authService = authService;
        this.modelMapper = modelMapper;
        this.key = key;
        this.blacklistStore = blacklistStore;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return ResponseEntity.ok(modelMapper.map(authService.register(dto), UserRegisterResponseDTO.class));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        String jti = (String) request.getAttribute("jti");

        if (jti == null) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        blacklistStore.addToken(jti);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/getRestrictedMaterial")
    public ResponseEntity<?> getRestrictedMaterial(@RequestHeader("Authorization") String header){
        return ResponseEntity.ok("THIS MATERIAL IS HIGHLY RESTRICTED");
    }


}

