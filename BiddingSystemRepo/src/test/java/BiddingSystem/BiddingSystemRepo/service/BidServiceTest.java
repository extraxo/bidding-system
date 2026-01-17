package BiddingSystem.BiddingSystemRepo.service;


import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.BidDTO;
import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.CreateBidInput;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.AuctionBidOnInvalidStatus;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.AuctionNotFound;
import BiddingSystem.BiddingSystemRepo.Exception.BidException.*;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.*;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Repository.AuctionRepository;
import BiddingSystem.BiddingSystemRepo.Repository.BidRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.Service.BidService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock
    private BidRepository bidRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BidService bidService;

    private static final Long USER_ID = 1L;
    private static final Long AUCTION_ID = 1L;

    // ---------- HELPERS ----------

    private void mockAuthenticatedUser(Long userId) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Auction createActiveAuction(User owner) {
        Auction auction = new Auction();
        auction.setId(AUCTION_ID);
        auction.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
        auction.setEndsAt(ZonedDateTime.now().plusMinutes(10));
        auction.setStartingPrice(BigDecimal.TEN);
        auction.setMinimumIncrement(BigDecimal.valueOf(2));

        Item item = new Item();
        item.setOwner(owner);
        auction.setItem(item);

        return auction;
    }

    // ---------- TESTS ----------

    @Test
    void givenInvalidAuctionId_shouldThrowAuctionNotFound() {
        CreateBidInput input = new CreateBidInput(AUCTION_ID, BigDecimal.valueOf(20));

        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.empty());

        AuctionNotFound ex = assertThrows(
                AuctionNotFound.class,
                () -> bidService.makeBid(input)
        );

        assertEquals("Auction with this id not found!", ex.getMessage());
    }

    @Test
    void givenNonActiveAuction_shouldFail() {
        Auction auction = new Auction();
        auction.setAuctionStatusEnum(AuctionStatusEnum.SCHEDULED);

        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.of(auction));

        CreateBidInput input = new CreateBidInput(AUCTION_ID, BigDecimal.TEN);

        AuctionBidOnInvalidStatus ex = assertThrows(
                AuctionBidOnInvalidStatus.class,
                () -> bidService.makeBid(input)
        );

        assertEquals("You can bid only on active auctions", ex.getMessage());
    }

    @Test
    void givenBidAfterAuctionEnd_shouldFail() {
        Auction auction = new Auction();
        auction.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
        auction.setEndsAt(ZonedDateTime.now().minusSeconds(1));

        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.of(auction));

        CreateBidInput input = new CreateBidInput(AUCTION_ID, BigDecimal.TEN);

        BidSentAfterEndTimeException ex = assertThrows(
                BidSentAfterEndTimeException.class,
                () -> bidService.makeBid(input)
        );

        assertEquals("Bid cannot be sent after end of auction", ex.getMessage());
    }

    @Test
    void ownerBiddingOwnItem_shouldFail() {
        mockAuthenticatedUser(USER_ID);

        User owner = new User();
        owner.setId(USER_ID);

        Auction auction = createActiveAuction(owner);

        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.of(auction));

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(owner));

        CreateBidInput input = new CreateBidInput(AUCTION_ID, BigDecimal.valueOf(20));

        OwnerBiddingOwnItemException ex = assertThrows(
                OwnerBiddingOwnItemException.class,
                () -> bidService.makeBid(input)
        );

        assertEquals("Owner of item cannot bid on its own listed items!", ex.getMessage());
    }

    @Test
    void bidLowerThanCurrent_shouldFail() {
        mockAuthenticatedUser(USER_ID);

        User owner = new User();
        owner.setId(2L);

        User bidder = new User();
        bidder.setId(USER_ID);

        Auction auction = createActiveAuction(owner);

        Bid currentBid = new Bid();
        currentBid.setPrice(BigDecimal.valueOf(50));

        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.of(auction));

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(bidder));

        when(bidRepository.findTopByAuctionOrderByPriceDesc(auction))
                .thenReturn(Optional.of(currentBid));

        CreateBidInput input = new CreateBidInput(AUCTION_ID, BigDecimal.valueOf(40));

        InvalidBidException ex = assertThrows(
                InvalidBidException.class,
                () -> bidService.makeBid(input)
        );

        assertEquals("New bid must be higher than the current price!", ex.getMessage());
    }

    @Test
    void overbiddingSelf_shouldFail() {
        mockAuthenticatedUser(USER_ID);

        User bidder = new User();
        bidder.setId(USER_ID);

        Auction auction = createActiveAuction(new User());

        Bid lastBid = new Bid();
        lastBid.setUser(bidder);
        lastBid.setPrice(BigDecimal.valueOf(50));

        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.of(auction));

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(bidder));

        when(bidRepository.findTopByAuctionOrderByPriceDesc(auction))
                .thenReturn(Optional.of(lastBid));

        CreateBidInput input = new CreateBidInput(AUCTION_ID, BigDecimal.valueOf(55));

        InvalidBidException ex = assertThrows(
                InvalidBidException.class,
                () -> bidService.makeBid(input)
        );

        assertEquals("You cannot overbid yourself!", ex.getMessage());
    }

    @Test
    void validBid_shouldBeSavedAndReturned() {
        mockAuthenticatedUser(USER_ID);

        User owner = new User();
        owner.setId(2L);

        User bidder = new User();
        bidder.setId(USER_ID);

        Auction auction = createActiveAuction(owner);

        CreateBidInput input = new CreateBidInput(AUCTION_ID, BigDecimal.valueOf(20));

        Bid savedBid = new Bid();
        savedBid.setPrice(input.getBidPrice());
        savedBid.setUser(bidder);

        BidDTO bidDTO = new BidDTO();
        bidDTO.setPrice(input.getBidPrice());

        when(auctionRepository.findById(AUCTION_ID))
                .thenReturn(Optional.of(auction));

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(bidder));

        when(bidRepository.save(any(Bid.class)))
                .thenReturn(savedBid);

        when(modelMapper.map(any(Bid.class), eq(BidDTO.class)))
                .thenReturn(bidDTO);

        BidDTO result = bidService.makeBid(input);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(20), result.getPrice());

        verify(bidRepository).save(any(Bid.class));
    }
}

