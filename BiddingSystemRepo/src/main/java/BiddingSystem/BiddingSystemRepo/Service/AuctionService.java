package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.AddItemToAuctionDTO;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.CreateAuctionInput;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.AuctionNotFound;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.AuctionPastStartingTimeException;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.ItemAlreadyInAuction;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.UserInsufficientBalanceException;
import BiddingSystem.BiddingSystemRepo.Exception.ItemExceptions.ItemNotFound;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Bid;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Repository.AuctionRepository;
import BiddingSystem.BiddingSystemRepo.Repository.BidRepository;
import BiddingSystem.BiddingSystemRepo.Repository.ItemRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    public AuctionService(ItemRepository itemRepository, AuctionRepository auctionRepository, BidRepository bidRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
    }

    private Long extractUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getPrincipal();
    }

    public void createAuction(CreateAuctionInput input) {

        ZonedDateTime now = ZonedDateTime.now();
        Long userId = extractUserId();

        Item Item = itemRepository.findByIdAndOwner_Id(input.getItemId(), userId)
                .orElseThrow(() -> new ItemNotFound("Item not found with id " + input.getItemId()));

        if (input.getStartingAt().isBefore(now.minusSeconds(2))) {
            throw new AuctionPastStartingTimeException("Auction cannot start more than 2 seconds ago");
        }

//        May be broken here by the AuctionStatusEnums.STATE
        if (auctionRepository.existsByItemIdAndAuctionStatusEnum(input.getItemId(), AuctionStatusEnum.ACTIVE) ||
                auctionRepository.existsByItemIdAndAuctionStatusEnum(input.getItemId(), AuctionStatusEnum.SCHEDULED)) {
            throw new ItemAlreadyInAuction("Current item already in active auction");
        }

        ZonedDateTime startTime =
                (input.getStartingAt() != null)
                        ? input.getStartingAt()
                        : now;

        AuctionStatusEnum initialStatus =
                startTime.isAfter(now)
                        ? AuctionStatusEnum.SCHEDULED
                        : AuctionStatusEnum.ACTIVE;

        Duration auctionDuration =
                (input.getDuration() != null)
                        ? input.getDuration()
                        : Duration.ofDays(1);

        Duration auctionDuration = (input.getDuration() != null)
                ? input.getDuration()
                : Duration.ofDays(1);

        Auction auction = new Auction();
        auction.setItem(Item);
        auction.setStartingAt(startTime);
        auction.setReservePrice(input.getReservePrice());
        auction.setStartingPrice(input.getStartingPrice());
        auction.setAuctionDuration(auctionDuration);
        auction.setAuctionStatusEnum(initialStatus);

        auctionRepository.save(auction);
    }

    // TODO: Extend the make publish logic
    public void makePublish(Long auctionId) throws Exception {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFound("Item not found with id " + auctionId));

        if (auction.getAuctionStatusEnum() != AuctionStatusEnum.DRAFT) {
            throw new Exception("Invalid change of auction status");
        }

        auction.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
        auctionRepository.save(auction);
    }

    @Transactional
    public void finishExpiredAuctions() {

        ZonedDateTime now = ZonedDateTime.now();

        List<Auction> auctionList = auctionRepository.findByAuctionStatusEnumAndEndsAtBefore(
                AuctionStatusEnum.ACTIVE,
                now);

        if (auctionList.isEmpty()) {
            return;
        }

        auctionList.forEach(this::closeAuction);
        auctionRepository.saveAll(auctionList);

    }

    // N + 1 Query
    public void closeAuction(Auction auction) {
        if (auction.getAuctionStatusEnum() != AuctionStatusEnum.ACTIVE) {
            return;
        }

        Optional<Bid> highestBid = bidRepository.findTopByAuctionOrderByPriceDesc(auction);

        if (highestBid.isPresent()
                && highestBid.get().getPrice().compareTo(auction.getReservePrice()) >= 0) {

            auction.setAuctionStatusEnum(AuctionStatusEnum.PENDING_PAYMENT);
            auction.setWinner(highestBid.get().getUser());
            auction.setWinnerBid(highestBid.get());

            auction.setPaymentDeadline(ZonedDateTime.now().plusMinutes(10));

        } else {
            auction.setAuctionStatusEnum(AuctionStatusEnum.ENDED_FAILED);
        }
    }

    @Transactional
    public void makePayment(Long auctionId) {
        Long userId =extractUserId();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFound("Auction not found"));

        if (auction.getAuctionStatusEnum() != AuctionStatusEnum.PENDING_PAYMENT) {
            throw new IllegalStateException("Auction is not awaiting payment");
        }

        if (ZonedDateTime.now().isAfter(auction.getPaymentDeadline())) {
            throw new IllegalStateException("Payment window expired");
        }

        if (!auction.getWinner().getId().equals(userId)) {
            throw new IllegalStateException("Only winner can pay");
        }

        User buyer = auction.getWinner();
        User seller = auction.getItem().getOwner();
        BigDecimal price = auction.getWinnerBid().getPrice();

        if (buyer.getBalance().compareTo(price) < 0) {
            throw new UserInsufficientBalanceException("Insufficient funds");
        }

        buyer.setBalance(buyer.getBalance().subtract(price));
        seller.setBalance(seller.getBalance().add(price));

        auction.getItem().setOwner(buyer);
        auction.setAuctionStatusEnum(AuctionStatusEnum.ENDED_SUCCESS);

    }

    @Transactional
    public void finishUnpaidAuctions() {
        ZonedDateTime now = ZonedDateTime.now();

        List<Auction> auctionList = auctionRepository.findByAuctionStatusEnumAndPaymentDeadlineBefore(
                AuctionStatusEnum.PENDING_PAYMENT,
                now);

        if (auctionList.isEmpty()) {
            return;
        }
        for (Auction auction : auctionList) {
            auction.setAuctionStatusEnum(AuctionStatusEnum.ENDED_FAILED);
            System.out.println("Auction failed due to unpaid item price");
        }
    }

    @Transactional
    public void makeAuctionActive() {

        ZonedDateTime now = ZonedDateTime.now();

        List<Auction> scheduledAuctions = auctionRepository
                .findByAuctionStatusEnumAndStartingAtLessThanEqual(AuctionStatusEnum.SCHEDULED, now);

        for (Auction auction : scheduledAuctions) {
            auction.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
        }

        auctionRepository.saveAll(scheduledAuctions);
    }

}
