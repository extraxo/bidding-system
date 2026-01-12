package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.CreateBidDTO;
import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.CreateBidInput;
import BiddingSystem.BiddingSystemRepo.Model.Entity.SystemBalance;
import BiddingSystem.BiddingSystemRepo.Service.SystemBalanceService;
import BiddingSystem.BiddingSystemRepo.Service.UserBalanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/user/balance")
public class UserBalanceController {

    private final UserBalanceService userBalanceService;
    private final SystemBalanceService systemBalanceService;

    public UserBalanceController(UserBalanceService userBalanceService, SystemBalanceService systemBalanceService) {
        this.userBalanceService = userBalanceService;
        this.systemBalanceService = systemBalanceService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestParam BigDecimal amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        userBalanceService.deposit(userId, amount);
        return ResponseEntity.ok("Money Deposited Successfully");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestParam BigDecimal amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        userBalanceService.withdraw(userId, amount);
        return ResponseEntity.ok("Money withdrawn successfully");
    }

    @GetMapping
    public ResponseEntity<?> viewBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        BigDecimal userBalance = userBalanceService.viewBalance(userId);
        return ResponseEntity.ok("User balance: " + userBalance);
    }

    @PostMapping("/adminOnlyController")
    @PreAuthorize("hasRole('RoleEnum.Admin')")
    public ResponseEntity<?> adminOnly(){

        systemBalanceService.getSystemBalance();

        return ResponseEntity.ok("Admin only");
    }
}
