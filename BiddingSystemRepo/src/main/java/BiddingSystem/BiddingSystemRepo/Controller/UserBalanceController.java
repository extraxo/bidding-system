package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.Service.UserBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/user/balance")
public class UserBalanceController {

    private final UserBalanceService userBalanceService;

    public UserBalanceController(UserBalanceService userBalanceService) {
        this.userBalanceService = userBalanceService;
    }

    // Deposit money
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestParam Long amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal(); // Get the logged-in user's ID

        userBalanceService.deposit(userId, amount);
        return ResponseEntity.ok("Money Deposited Successfully");
    }

    // Withdraw money
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestParam Long amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal(); // Get the logged-in user's ID

        userBalanceService.withdraw(userId, amount);
        return ResponseEntity.ok("Money withdrawn successfully");
    }

    // View balance (GET request as it's a read operation)
    @GetMapping
    public ResponseEntity<?> viewBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal(); // Get the logged-in user's ID

        BigDecimal userBalance = userBalanceService.viewBalance(userId);
        return ResponseEntity.ok("User balance: " + userBalance);
    }
}
