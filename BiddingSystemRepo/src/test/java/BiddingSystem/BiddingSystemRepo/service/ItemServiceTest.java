package BiddingSystem.BiddingSystemRepo.service;


import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.OutputItemDTO;
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
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
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

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ItemService itemService;


    @Test
    public void givenValidItemInput_shouldAddItemSuccessfully() {
        // Arrange
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

        // when(userRepository.findUserById(1L))
        //         .thenReturn(Optional.of(user));

        when(itemRepository.existsByOwnerAndName(user, dto.getName()))
                .thenReturn(false);

        // Mock DTO → Entity mapping
        Item mappedItem = new Item();
        mappedItem.setName(dto.getName());
        mappedItem.setDescription(dto.getDescription());
        mappedItem.setItemCategoryEnum(dto.getItemCategoryEnum());
        mappedItem.setItemConditionEnum(dto.getItemConditionEnum());

        when(modelMapper.map(dto, Item.class))
                .thenReturn(mappedItem);

        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Mock Entity → ResponseDTO mapping
        OutputItemDTO responseDTO = new OutputItemDTO();
        responseDTO.setName(dto.getName());
        responseDTO.setDescription(dto.getDescription());

        when(modelMapper.map(any(Item.class), eq(OutputItemDTO.class)))
                .thenReturn(responseDTO);

        // Act
        OutputItemDTO result = itemService.addItem(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());

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
        // when(userRepository.findUserById(1L))
        //         .thenReturn(Optional.of(user));

        Item item = new Item();
        item.setOwner(user);

        when(itemRepository.existsByOwnerAndName(user,dto.getName()))
                .thenReturn(true);

        ItemAlreadyInUserInventory exception = assertThrows(ItemAlreadyInUserInventory.class, () ->
                itemService.addItem(dto));

        assertEquals("Item with same name already in current user's inventory!", exception.getMessage());
    }

    @Test
    public void givenItemsExistForUser_shouldReturnMappedOutputItemDTOs() {
        // Arrange
        Long userId = 1L;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        // Mock Items
        Item item1 = new Item();
        item1.setName("Pot of Greed");
        item1.setDescription("This is the pot of greed from Yu-Gi-Oh");
        item1.setItemCategoryEnum(ItemCategoryEnum.COLLECTIBLES);
        item1.setItemConditionEnum(ItemConditionEnum.NEW);

        Item item2 = new Item();
        item2.setName("Yu Gi Oh card");
        item2.setDescription("this card has special powers");
        item2.setItemCategoryEnum(ItemCategoryEnum.COLLECTIBLES);
        item2.setItemConditionEnum(ItemConditionEnum.NEW);

        Item item3 = new Item();
        item3.setName("newItem");
        item3.setDescription("okay");
        item3.setItemCategoryEnum(ItemCategoryEnum.ELECTRONICS);
        item3.setItemConditionEnum(ItemConditionEnum.NEW);

        Item item4 = new Item();
        item4.setName("newItem2");
        item4.setDescription("okay");
        item4.setItemCategoryEnum(ItemCategoryEnum.ELECTRONICS);
        item4.setItemConditionEnum(ItemConditionEnum.NEW);

        when(itemRepository.findAllByOwnerId(userId))
                .thenReturn(List.of(item1, item2, item3, item4));

        // Mock mappings
        when(modelMapper.map(item1, OutputItemDTO.class))
                .thenReturn(new OutputItemDTO(
                        "Pot of Greed",
                        "This is the pot of greed from Yu-Gi-Oh",
                        ItemCategoryEnum.COLLECTIBLES,
                        ItemConditionEnum.NEW
                ));

        when(modelMapper.map(item2, OutputItemDTO.class))
                .thenReturn(new OutputItemDTO(
                        "Yu Gi Oh card",
                        "this card has special powers",
                        ItemCategoryEnum.COLLECTIBLES,
                        ItemConditionEnum.NEW
                ));

        when(modelMapper.map(item3, OutputItemDTO.class))
                .thenReturn(new OutputItemDTO(
                        "newItem",
                        "okay",
                        ItemCategoryEnum.ELECTRONICS,
                        ItemConditionEnum.NEW
                ));

        when(modelMapper.map(item4, OutputItemDTO.class))
                .thenReturn(new OutputItemDTO(
                        "newItem2",
                        "okay",
                        ItemCategoryEnum.ELECTRONICS,
                        ItemConditionEnum.NEW
                ));

        // Act
        List<OutputItemDTO> result = itemService.getAllItems();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());

        assertEquals("Pot of Greed", result.get(0).getName());
        assertEquals("Yu Gi Oh card", result.get(1).getName());
        assertEquals("newItem", result.get(2).getName());
        assertEquals("newItem2", result.get(3).getName());

        verify(itemRepository).findAllByOwnerId(userId);
    }

    @Test
    public void givenNoItemsForUser_shouldReturnEmptyList() {
        Long userId = 1L;

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(itemRepository.findAllByOwnerId(userId))
                .thenReturn(List.of());

        List<OutputItemDTO> result = itemService.getAllItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
