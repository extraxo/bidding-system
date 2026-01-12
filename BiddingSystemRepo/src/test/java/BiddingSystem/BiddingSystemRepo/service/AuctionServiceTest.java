package BiddingSystem.BiddingSystemRepo.service;

import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.CreateAuctionInput;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.AuctionPastStartingTimeException;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.ItemAlreadyInAuction;
import BiddingSystem.BiddingSystemRepo.Exception.ItemExceptions.ItemNotFound;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Bid;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

//TODO : Inject Clock
//TODO: Add test no starting time import case
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
    private static final BigDecimal MINIMUM_INCREMENT = BigDecimal.valueOf(2);

    private CreateAuctionInput createValidInput(ZonedDateTime startingAt) {
        return new CreateAuctionInput(
                ITEM_ID,
                startingAt,
                DEFAULT_DURATION,
                STARTING_PRICE,
                RESERVE_PRICE,
                MINIMUM_INCREMENT
                
        );
    }

    private CreateAuctionInput createInput(
            ZonedDateTime startingAt,
            BigDecimal startingPrice,
            BigDecimal reservePrice
    ) {
        return new CreateAuctionInput(
                ITEM_ID,
                startingAt,
                DEFAULT_DURATION,
                startingPrice,
                reservePrice,
                MINIMUM_INCREMENT
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

    private void mockAuthenticatedUser(Long userId) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Test
    public void givenInvalidItemId_shouldFail(){

        CreateAuctionInput createAuctionInput = createValidInput(ZonedDateTime.now());

        mockItemNotFound();

        ItemNotFound exception = assertThrows(ItemNotFound.class, () -> auctionService.createAuction(createAuctionInput));

        assertEquals("Item not found with id " + createAuctionInput.getItemId(), exception.getMessage());

    }

    @Test
    public void givenActiveAuctionStatus_addingItemToAuctionShouldFail(){

        CreateAuctionInput createAuctionInput = createValidInput(ZonedDateTime.now());

        mockItemFound();

        when(auctionRepository.existsByItemIdAndAuctionStatusEnum(ITEM_ID, AuctionStatusEnum.ACTIVE))
                .thenReturn(true);


        ItemAlreadyInAuction exception = assertThrows(ItemAlreadyInAuction.class, () -> auctionService.createAuction(createAuctionInput));

        assertEquals("Current item already in active auction", exception.getMessage());

    }

    @Test
    public void givenActiveScheduledStatus_addingItemToAuctionShouldFail(){

        mockAuthenticatedUser(USER_ID);

        CreateAuctionInput createAuctionInput = createValidInput(ZonedDateTime.now());


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

        mockAuthenticatedUser(USER_ID);


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
                reservePrice,
                MINIMUM_INCREMENT
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

        CreateAuctionInput createAuctionInput = createValidInput(startingAt);

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

        CreateAuctionInput createAuctionInput = createValidInput(startingAt);

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

    @Test
    public void givenNegativeReservePrice_shouldFail() {
        CreateAuctionInput input = createInput(
                ZonedDateTime.now().plusSeconds(1),
                BigDecimal.TEN,
                BigDecimal.valueOf(-1)
        );

        mockItemFound();

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> auctionService.createAuction(input));

        assertEquals("Reserve price must be positive", ex.getMessage());
    }

    @Test
    public void givenNegativeStartingPrice_shouldFail() {
        CreateAuctionInput input = createInput(
                ZonedDateTime.now().plusSeconds(1),
                BigDecimal.valueOf(-1),
                BigDecimal.TEN
        );
        mockItemFound();

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> auctionService.createAuction(input));

        assertEquals("Starting price must be positive", ex.getMessage());
    }

    @Test
    public void givenStartingBiggerThanReserve_shouldFail() {
        CreateAuctionInput input = createInput(
                ZonedDateTime.now().plusSeconds(1),
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(20)
        );
        mockItemFound();

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> auctionService.createAuction(input));

        assertEquals("Reserve price must be greater than starting price", ex.getMessage());
    }

    @Test
    public void givenDurationShorterThan10Minutes_shouldFail() {
        CreateAuctionInput input = new CreateAuctionInput(
                ITEM_ID,
                ZonedDateTime.now().plusSeconds(1),
                Duration.ofMinutes(5),
                STARTING_PRICE,
                RESERVE_PRICE,
                MINIMUM_INCREMENT
        );

        mockItemFound();

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> auctionService.createAuction(input));

        assertEquals("Duration must be longer than 10 minutes!", ex.getMessage());
    }

    @Test
    public void givenDurationLongerThan7Days_shouldFail() {
        CreateAuctionInput input = new CreateAuctionInput(
                ITEM_ID,
                ZonedDateTime.now().plusSeconds(1),
                Duration.ofDays(8),
                STARTING_PRICE,
                RESERVE_PRICE,
                MINIMUM_INCREMENT
        );

        mockItemFound();

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> auctionService.createAuction(input));

        assertEquals("Duration must be shorter than 7 days!", ex.getMessage());
    }

    @Test
    public void givenNoExpiredAuctions_shouldDoNothing(){

        when(auctionRepository.findByAuctionStatusEnumAndEndsAtBefore(
                eq(AuctionStatusEnum.ACTIVE),
                any(ZonedDateTime.class)
        )).thenReturn(List.of());

        auctionService.finishExpiredAuctions();

        verify(auctionRepository, never()).saveAll(any());

    }

    @Test
    void givenExpiredAuctionWithoutBid_shouldEndFailed() {
        Auction auction = new Auction();
        auction.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
        auction.setReservePrice(BigDecimal.TEN);

        when(auctionRepository.findByAuctionStatusEnumAndEndsAtBefore(
                eq(AuctionStatusEnum.ACTIVE),
                any(ZonedDateTime.class)
        )).thenReturn(List.of(auction));

        when(bidRepository.findTopByAuctionOrderByPriceDesc(auction))
                .thenReturn(Optional.empty());

        auctionService.finishExpiredAuctions();

        assertEquals(AuctionStatusEnum.ENDED_FAILED, auction.getAuctionStatusEnum());
        verify(auctionRepository).saveAll(List.of(auction));
    }

    @Test
    void expiredAuction_withValidBid_shouldBePendingPayment() {
        User bidder = new User();
        Bid bid = new Bid();
        bid.setPrice(BigDecimal.valueOf(100));
        bid.setUser(bidder);

        Auction auction = new Auction();
        auction.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
        auction.setReservePrice(BigDecimal.valueOf(50));

        when(auctionRepository.findByAuctionStatusEnumAndEndsAtBefore(
                eq(AuctionStatusEnum.ACTIVE),
                any()
        )).thenReturn(List.of(auction));

        when(bidRepository.findTopByAuctionOrderByPriceDesc(auction))
                .thenReturn(Optional.of(bid));

        auctionService.finishExpiredAuctions();

        assertEquals(AuctionStatusEnum.PENDING_PAYMENT, auction.getAuctionStatusEnum());
        assertEquals(bidder, auction.getWinner());
        assertEquals(bid, auction.getWinnerBid());
    }


    @Test
    void unpaidAuction_shouldFail() {
        Auction auction = new Auction();
        auction.setAuctionStatusEnum(AuctionStatusEnum.PENDING_PAYMENT);

        when(auctionRepository.findByAuctionStatusEnumAndPaymentDeadlineBefore(
                eq(AuctionStatusEnum.PENDING_PAYMENT),
                any()
        )).thenReturn(List.of(auction));

        auctionService.finishUnpaidAuctions();

        assertEquals(AuctionStatusEnum.ENDED_FAILED, auction.getAuctionStatusEnum());
    }

    @Test
    void scheduledAuction_shouldBecomeActive() {
        Auction auction = new Auction();
        auction.setAuctionStatusEnum(AuctionStatusEnum.SCHEDULED);

        when(auctionRepository.findByAuctionStatusEnumAndStartingAtLessThanEqual(
                eq(AuctionStatusEnum.SCHEDULED),
                any()
        )).thenReturn(List.of(auction));

        auctionService.makeAuctionActive();

        assertEquals(AuctionStatusEnum.ACTIVE, auction.getAuctionStatusEnum());
        verify(auctionRepository).saveAll(List.of(auction));
    }

    @Test
    void payment_successful() {
        User buyer = new User();
        buyer.setId(USER_ID);
        buyer.setBalance(BigDecimal.valueOf(100));

        User seller = new User();
        seller.setBalance(BigDecimal.ZERO);

        Item item = new Item();
        item.setOwner(seller);

        Bid bid = new Bid();
        bid.setPrice(BigDecimal.valueOf(50));

        Auction auction = new Auction();
        auction.setAuctionStatusEnum(AuctionStatusEnum.PENDING_PAYMENT);
        auction.setWinner(buyer);
        auction.setWinnerBid(bid);
        auction.setItem(item);
        auction.setPaymentDeadline(ZonedDateTime.now().plusMinutes(5));

        when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction));

        auctionService.makePayment(1L);

        assertEquals(BigDecimal.valueOf(50), seller.getBalance());
        assertEquals(BigDecimal.valueOf(50), buyer.getBalance());
        assertEquals(AuctionStatusEnum.ENDED_SUCCESS, auction.getAuctionStatusEnum());
    }



}