package BiddingSystem.BiddingSystemRepo.service;


import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.RegisterItemDTO;
import BiddingSystem.BiddingSystemRepo.Exception.ItemExceptions.ItemAlreadyInUserInventory;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import BiddingSystem.BiddingSystemRepo.Repository.ItemRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.Service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest{

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemService itemService;


    @Test
    public void givenValidItemInput_shouldAddItemSuccessfully(){
        RegisterItemDTO dto = new RegisterItemDTO(
                "SomeItemName",
                "This item is kinda legit",
                ItemCategoryEnum.COLLECTIBLES,
                ItemConditionEnum.NEW
        );

        User user = new User();
        user.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(1L);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Mock repositories
        when(userRepository.findUserById(1L))
                .thenReturn(Optional.of(user));


        when(itemRepository.existsByOwnerAndName(user,dto.getName()))
                .thenReturn(false);

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Item item = itemService.addItem(dto);

        assertNotNull(item);
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getItemCategoryEnum(), item.getItemCategoryEnum());
        assertEquals(dto.getItemConditionEnum(), item.getItemConditionEnum());
        assertEquals(user, item.getOwner());

        // Verify behavior
        verify(itemRepository).existsByOwnerAndName(user, dto.getName());
        verify(itemRepository).save(any(Item.class));
    }


    @Test
    public void givenDuplicatedItemNamaAndSameOwner_shouldFail(){

        RegisterItemDTO dto = new RegisterItemDTO(
                "SomeItemName",
                "This item is kinda legit",
                ItemCategoryEnum.COLLECTIBLES,
                ItemConditionEnum.NEW
        );

        User user = new User();
        user.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(1L);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Mock repositories
        when(userRepository.findUserById(1L))
                .thenReturn(Optional.of(user));

        Item item = new Item();
        item.setOwner(user);

        when(itemRepository.existsByOwnerAndName(user,dto.getName()))
                .thenReturn(true);

        ItemAlreadyInUserInventory exception = assertThrows(ItemAlreadyInUserInventory.class, () ->
                itemService.addItem(dto));

        assertEquals("Item with same name already in current user's inventory!", exception.getMessage());
    }

}
