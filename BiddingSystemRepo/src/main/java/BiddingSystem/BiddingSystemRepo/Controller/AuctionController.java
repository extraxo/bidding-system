package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.AddItemToAuctionDTO;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.CreateAuctionInput;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.ExposeAuctionDTO;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auction")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Operation(
            summary = "Create auction"
    )
    @PostMapping("/")
    public ResponseEntity<?> addItem(
            @RequestBody @Valid AddItemToAuctionDTO addItemToAuctionDTO
    ) {

        CreateAuctionInput input = new CreateAuctionInput(
                addItemToAuctionDTO.getItemId(),
                addItemToAuctionDTO.getStartingAt(),
                addItemToAuctionDTO.getAuctionDuration(),
                addItemToAuctionDTO.getStartingPrice(),
                addItemToAuctionDTO.getReservePrice(),
                addItemToAuctionDTO.getMinimumIncrement()
        );
        auctionService.createAuction(input);
        return ResponseEntity.ok("Added successfully");
    }

    @Operation(
            summary = "List all auctions",
            description = "Returns a list of all auctions with item, owner and bid history info with filter options"
    )
    @GetMapping("/")
    public ResponseEntity<?> showAllAuctions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) ZonedDateTime endsBefore
    ){
        AuctionStatusEnum enumStatus = null;

        if (status != null) {
            enumStatus = AuctionStatusEnum.valueOf(status.toUpperCase());
        }

        List<ExposeAuctionDTO> auctionList = auctionService.showAllAuctions(enumStatus,minPrice,endsBefore);
        return ResponseEntity.ok(auctionList);
    }

    @Operation(
            summary = "Get specific auction"
    )
    @GetMapping("/{auctionId}")
    public ResponseEntity<ExposeAuctionDTO> getAuction(@PathVariable("auctionId") Long auctionId){
        ExposeAuctionDTO dto = auctionService.getAuctionById(auctionId);
        return ResponseEntity.ok(dto);
    }

}
