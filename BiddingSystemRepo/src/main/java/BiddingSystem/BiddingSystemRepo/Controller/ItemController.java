package BiddingSystem.BiddingSystemRepo.Controller;


import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.RegisterItemDTO;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Service.ItemService;
import BiddingSystem.BiddingSystemRepo.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/item")
@SecurityRequirement(name = "authorization")
@Validated
public class ItemController {

    private final ItemService itemService;
    private final ModelMapper modelMapper;

    public ItemController(ItemService itemService, ModelMapper modelMapper) {
        this.itemService = itemService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/addItem")
    public ResponseEntity<?> addItem(@Valid @RequestBody RegisterItemDTO registerItemDTO) throws Exception {
        itemService.addItem(modelMapper.map(registerItemDTO, Item.class));
        return ResponseEntity.ok("Item added successfully");
    }


}
