package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.AddItemToAuctionDTO;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.CreateAuctionInput;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.ExposeAuctionDTO;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Tag(
        name = "Auction management"
)
@RestController
@RequestMapping("/api/v1/auction")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Operation(
            summary = "Create auction",
            description = "Example valid formats. Starting time - **2026-01-13T10:14:39.240Z** ( All date times must be provided with **UTC (London / GMT)**). Duration - **PT10M, PT30M, PT1H, PT2H30M, P1D**"
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
        ExposeAuctionDTO exposeAuctionDTO =  auctionService.createAuction(input);
        return ResponseEntity.ok(exposeAuctionDTO);
    }

    @Operation(
            summary = "List all auctions",
            description = "Returns a list of all auctions with item, owner and bid history info with filter options. Default values of filters: status - **ACTIVE**, minPrice - **0**, endsBefore - **now() + 7 days offset**"
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
            summary = "Get specific auction",
            description = "Auction must be payed within 10 minutes by winner, otherwise auction is failed."
    )
    @GetMapping("/{auctionId}")
    public ResponseEntity<ExposeAuctionDTO> getAuction(@PathVariable("auctionId") Long auctionId){
        ExposeAuctionDTO dto = auctionService.getAuctionById(auctionId);
        return ResponseEntity.ok(dto);
    }

}
