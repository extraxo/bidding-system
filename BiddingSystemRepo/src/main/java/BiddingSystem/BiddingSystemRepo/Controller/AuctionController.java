package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.AddItemToAuctionDTO;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.CreateAuctionInput;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.MakePaymentDTO;
import BiddingSystem.BiddingSystemRepo.Service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auction")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }


    @PostMapping("/addAuction")
    public ResponseEntity<?> addItem(
            @RequestBody @Valid AddItemToAuctionDTO addItemToAuctionDTO
    ) {

        CreateAuctionInput input = new CreateAuctionInput(
                addItemToAuctionDTO.getItemId(),
                addItemToAuctionDTO.getStartingAt(),
                addItemToAuctionDTO.getAuctionDuration(),
                addItemToAuctionDTO.getStartingPrice(),
                addItemToAuctionDTO.getReservePrice()
        );
        auctionService.createAuction(input);
        return ResponseEntity.ok("Added successfully");
    }

    public ResponseEntity<?> makeAuctionPayment(@RequestBody MakePaymentDTO makePaymentDTO){
        auctionService.makePayment(makePaymentDTO.getAuctionId());
        return ResponseEntity.ok("PaymentSuccessfully");
    }

}
