package BiddingSystem.BiddingSystemRepo.service;

import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.CreateAuctionInput;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.AuctionPastStartingTimeException;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.ItemAlreadyInAuction;
import BiddingSystem.BiddingSystemRepo.Exception.ItemExceptions.ItemNotFound;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Repository.AuctionRepository;
import BiddingSystem.BiddingSystemRepo.Repository.BidRepository;
import BiddingSystem.BiddingSystemRepo.Repository.ItemRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.Service.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//TODO : Inject Clock
@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuctionService auctionService;

    private static final Long USER_ID = 1L;
    private static final Long ITEM_ID = 1L;
    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(10);
    private static final BigDecimal STARTING_PRICE = BigDecimal.TEN;
    private static final BigDecimal RESERVE_PRICE = BigDecimal.valueOf(20);

    private CreateAuctionInput createInput(ZonedDateTime startingAt) {
        return new CreateAuctionInput(
                ITEM_ID,
                startingAt,
                DEFAULT_DURATION,
                STARTING_PRICE,
                RESERVE_PRICE
        );
    }

    private Item mockItemFound() {
        Item item = new Item();
        item.setId(ITEM_ID);
        when(itemRepository.findByIdAndOwner_Id(ITEM_ID, USER_ID))
                .thenReturn(Optional.of(item));
        return item;
    }

    private void mockItemNotFound() {
        when(itemRepository.findByIdAndOwner_Id(ITEM_ID, USER_ID))
                .thenReturn(Optional.empty());
    }

    private void mockNoExistingAuctions() {
        when(auctionRepository.existsByItemIdAndAuctionStatusEnum(ITEM_ID, AuctionStatusEnum.ACTIVE))
                .thenReturn(false);
        when(auctionRepository.existsByItemIdAndAuctionStatusEnum(ITEM_ID, AuctionStatusEnum.SCHEDULED))
                .thenReturn(false);
    }


    @BeforeEach
    void setUpSecurityContext() {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(1L);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void givenInvalidItemId_shouldFail(){

        CreateAuctionInput createAuctionInput = createInput(ZonedDateTime.now());

        mockItemNotFound();

        ItemNotFound exception = assertThrows(ItemNotFound.class, () -> auctionService.createAuction(createAuctionInput));

        assertEquals("Item not found with id " + createAuctionInput.getItemId(), exception.getMessage());

    }

    @Test
    public void givenActiveAuctionStatus_addingItemToAuctionShouldFail(){

        CreateAuctionInput createAuctionInput = createInput(ZonedDateTime.now());

        mockItemFound();

        when(auctionRepository.existsByItemIdAndAuctionStatusEnum(ITEM_ID, AuctionStatusEnum.ACTIVE))
                .thenReturn(true);


        ItemAlreadyInAuction exception = assertThrows(ItemAlreadyInAuction.class, () -> auctionService.createAuction(createAuctionInput));

        assertEquals("Current item already in active auction", exception.getMessage());

    }

    @Test
    public void givenActiveScheduledStatus_addingItemToAuctionShouldFail(){

        CreateAuctionInput createAuctionInput = createInput(ZonedDateTime.now());


        mockItemFound();


        when(auctionRepository.existsByItemIdAndAuctionStatusEnum(ITEM_ID, AuctionStatusEnum.ACTIVE))
                .thenReturn(false);

        when(auctionRepository.existsByItemIdAndAuctionStatusEnum(ITEM_ID, AuctionStatusEnum.SCHEDULED))
                .thenReturn(true);

        ItemAlreadyInAuction exception = assertThrows(ItemAlreadyInAuction.class, () -> auctionService.createAuction(createAuctionInput));

        assertEquals("Current item already in active auction", exception.getMessage());

    }

    @Test
    public void givenPastAuctionStartingTime_addingItemToAuctionShouldFail(){

        Duration subtractedPeriodToBePassed = Duration.ofSeconds(3);
        ZonedDateTime startingAt = ZonedDateTime.now().minus(subtractedPeriodToBePassed);
        Duration duration = Duration.ofMinutes(10);
        BigDecimal startingPrice = new BigDecimal(10);
        BigDecimal reservePrice = new BigDecimal(20);

        CreateAuctionInput createAuctionInput = new CreateAuctionInput(
                1L,
                startingAt,
                duration,
                startingPrice,
                reservePrice
        );


        mockItemFound();


        AuctionPastStartingTimeException exception = assertThrows(
                AuctionPastStartingTimeException.class,
                () -> auctionService.createAuction(createAuctionInput)
        );

        assertEquals("Auction cannot start more than 2 seconds ago", exception.getMessage());

    }

    @Test
    public void givenPresentAuctionStartingTime_addingItemToAuctionShouldSuccess(){

        ZonedDateTime now = ZonedDateTime.now();
        Duration subtractedPeriodToBePassed = Duration.ofSeconds(0);
        ZonedDateTime startingAt = now.minus(subtractedPeriodToBePassed);

        CreateAuctionInput createAuctionInput = createInput(startingAt);

        Item item = mockItemFound();

        mockNoExistingAuctions();

        ArgumentCaptor<Auction> auctionCaptor = ArgumentCaptor.forClass(Auction.class);
        auctionService.createAuction(createAuctionInput);
        verify(auctionRepository).save(auctionCaptor.capture());
        Auction savedAuction = auctionCaptor.getValue();

        assertEquals(item, savedAuction.getItem());
        assertEquals(startingAt, savedAuction.getStartingAt());
        assertEquals(DEFAULT_DURATION, savedAuction.getAuctionDuration());
        assertEquals(RESERVE_PRICE, savedAuction.getReservePrice());
        assertEquals(STARTING_PRICE, savedAuction.getStartingPrice());
        assertEquals(AuctionStatusEnum.ACTIVE, savedAuction.getAuctionStatusEnum());
    }

    @Test
    public void givenFutureAuctionStartingTime_addingItemToAuctionShouldSuccess(){

        ZonedDateTime now = ZonedDateTime.now();
        Duration futureOffset = Duration.ofSeconds(1);
        ZonedDateTime startingAt = now.plus(futureOffset);

        CreateAuctionInput createAuctionInput = createInput(startingAt);

        Item item = mockItemFound();

        mockNoExistingAuctions();

        ArgumentCaptor<Auction> auctionCaptor = ArgumentCaptor.forClass(Auction.class);
        auctionService.createAuction(createAuctionInput);
        verify(auctionRepository).save(auctionCaptor.capture());
        Auction savedAuction = auctionCaptor.getValue();

        assertEquals(item, savedAuction.getItem());
        assertEquals(startingAt, savedAuction.getStartingAt());
        assertEquals(DEFAULT_DURATION, savedAuction.getAuctionDuration());
        assertEquals(RESERVE_PRICE, savedAuction.getReservePrice());
        assertEquals(STARTING_PRICE, savedAuction.getStartingPrice());
        assertEquals(AuctionStatusEnum.SCHEDULED, savedAuction.getAuctionStatusEnum());
    }




}
