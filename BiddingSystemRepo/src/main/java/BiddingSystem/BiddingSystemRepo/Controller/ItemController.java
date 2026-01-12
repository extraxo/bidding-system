package BiddingSystem.BiddingSystemRepo.Controller;


import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.OutputItemDTO;
import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.RegisterItemDTO;
import BiddingSystem.BiddingSystemRepo.Service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/item")
// TODO: CRUD OPERATIONS ABOUT ITEM
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(
            summary = "Add item to your inventory"
    )
    @PostMapping("/")
    public ResponseEntity<?> addItem(@Valid @RequestBody RegisterItemDTO registerItemDTO) throws Exception {
        OutputItemDTO response = itemService.addItem(registerItemDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all items that belong to you"
    )
    @GetMapping("/")
    public ResponseEntity<?> getAllUserItems(){
        return ResponseEntity.ok(itemService.getAllItems());
    }


}
