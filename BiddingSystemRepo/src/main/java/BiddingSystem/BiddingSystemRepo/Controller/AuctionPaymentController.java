package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.Service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auction/payment")
public class AuctionPaymentController {

    private final AuctionService auctionService;

    public AuctionPaymentController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Operation(
            summary = "Make auction payment"
    )
    @PostMapping("/{auctionId}")
    public ResponseEntity<?> makeAuctionPayment(@PathVariable("auctionId") Long auctionId ){
        auctionService.makePayment(auctionId);
        return ResponseEntity.ok("Payment Successfully");
    }
}
