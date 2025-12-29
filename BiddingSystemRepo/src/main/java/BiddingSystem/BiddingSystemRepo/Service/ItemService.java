package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.RegisterItemDTO;
import BiddingSystem.BiddingSystemRepo.Exception.ItemExceptions.ItemAlreadyInUserInventory;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Repository.AuctionRepository;
import BiddingSystem.BiddingSystemRepo.Repository.ItemRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;



    public ItemService(ItemRepository itemRepository,UserRepository userRepository,ModelMapper modelMapper){
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public void addItem(Item item) throws ItemAlreadyInUserInventory {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("No such user!"));

        if (itemRepository.existsByOwnerAndName(user,item.getName())){
            throw new ItemAlreadyInUserInventory("Item with same name already in current user's inventory!");
        }

        item.setOwner(user);

        itemRepository.save(item);
    }

}
