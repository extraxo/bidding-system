package BiddingSystem.BiddingSystemRepo.Controller;


import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.RegisterItemDTO;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Response.UserResponseDTO.CreateItemResponseDTO;
import BiddingSystem.BiddingSystemRepo.Response.UserResponseDTO.UserRegisterResponseDTO;
import BiddingSystem.BiddingSystemRepo.Service.ItemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/")
    public ResponseEntity<?> addItem(@Valid @RequestBody RegisterItemDTO registerItemDTO) throws Exception {
        return ResponseEntity.ok(modelMapper.map(itemService.addItem(registerItemDTO), CreateItemResponseDTO.class));
    }


}
