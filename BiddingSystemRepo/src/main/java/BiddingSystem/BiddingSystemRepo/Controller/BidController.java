package BiddingSystem.BiddingSystemRepo.Controller;


import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.CreateAuctionInput;
import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.CreateBidDTO;
import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.CreateBidInput;
import BiddingSystem.BiddingSystemRepo.Service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bid")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService){
        this.bidService = bidService;
    }

    @Operation(
            summary = "Make bid",
            description = "Handles creating a bid"
    )
    @PostMapping("/")
    public ResponseEntity<?> makeBid(@RequestBody @Valid CreateBidDTO createBidDTO){

        CreateBidInput createAuctionInput = new CreateBidInput(
                createBidDTO.getAuctionId(),
                createBidDTO.getBidPrice()
        );

        bidService.makeBid(createAuctionInput);
//        TODO: RETURN DATA NOT TEXT
        return ResponseEntity.ok("Bid Created successfully");
    }


}
