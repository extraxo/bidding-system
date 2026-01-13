package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.SystemBalanceDTO.SystemBalanceResponseDTO;
import BiddingSystem.BiddingSystemRepo.Service.SystemBalanceService;
import BiddingSystem.BiddingSystemRepo.Service.UserBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "System balance"
)
@RestController
@RequestMapping("/api/v1/systemBalance")
public class SystemBalanceController {

    private final SystemBalanceService systemBalanceService;

    public SystemBalanceController(SystemBalanceService systemBalanceService) {
        this.systemBalanceService = systemBalanceService;
    }

    @Operation(
            summary = "Access system balance accumulated through deposit/withdraw fees",
            description = "Must be admin to get access (For test: admin@abv.bg/admin)"
    )
    @GetMapping("/")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<?> adminOnly(){
//        TODO: CATCH 401 ERROR WITH CUSTOM TEXT
        return ResponseEntity.ok(systemBalanceService.getSystemBalance());
    }

}
