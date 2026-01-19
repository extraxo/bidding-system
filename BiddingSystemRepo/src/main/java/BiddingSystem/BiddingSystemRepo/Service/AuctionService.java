package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.CreateAuctionInput;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.ExposeAuctionDTO;
import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.OutputItemDTO;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.*;
import BiddingSystem.BiddingSystemRepo.Exception.BidException.InvalidBidException;
import BiddingSystem.BiddingSystemRepo.Exception.ItemExceptions.ItemNotFound;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Bid;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Repository.AuctionRepository;
import BiddingSystem.BiddingSystemRepo.Repository.BidRepository;
import BiddingSystem.BiddingSystemRepo.Repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AuctionService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Europe/Sofia");


    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    public AuctionService(ItemRepository itemRepository, AuctionRepository auctionRepository, BidRepository bidRepository, ModelMapper modelMapper, EmailService emailService) {
        this.itemRepository = itemRepository;
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    private Long extractUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getPrincipal();
    }

    private void validatePrices(CreateAuctionInput input) {
        if (input.getStartingPrice().signum() <= 0) {
            throw new IllegalArgumentException("Starting price must be positive");
        }

        if (input.getReservePrice().signum() <= 0) {
            throw new IllegalArgumentException("Reserve price must be positive");
        }

        if (input.getMinimumIncrement().signum() <= 0) {
            throw new IllegalArgumentException("Minimum increment must be positive");
        }

        if (input.getReservePrice().compareTo(input.getStartingPrice()) <= 0) {
            throw new IllegalArgumentException("Reserve price must be greater than starting price");
        }
    }

    private void validateDurationTime(CreateAuctionInput input){
        Duration minDuration = Duration.ofMinutes(10);
        Duration maxDuration = Duration.ofDays(7);

        if (input.getDuration().compareTo(minDuration) < 0){
            throw new IllegalArgumentException("Duration must be longer than 10 minutes!");
        }

        if (input.getDuration().compareTo(maxDuration) > 0){
            throw new IllegalArgumentException("Duration must be shorter than 7 days!");
        }
    }

    public ExposeAuctionDTO createAuction(CreateAuctionInput input) {

        ZonedDateTime now = ZonedDateTime.now();
        Long userId = extractUserId();

        Instant nowCompare = Instant.now();
        Instant startCompare = input.getStartingAt().toInstant();

        Item Item = itemRepository.findByIdAndOwner_Id(input.getItemId(), userId)
                .orElseThrow(() -> new ItemNotFound("Item not found with id " + input.getItemId()));


        if (startCompare.isBefore(nowCompare.minusSeconds(2))) {
            throw new AuctionPastStartingTimeException("Auction cannot start more than 2 seconds ago!");
        }

        if (input.getStartingAt().isAfter(now.plusWeeks(2))) {
            throw new AuctionPastStartingTimeException("Auction cannot start more than 2 weeks from present!");
        }

        validatePrices(input);
        validateDurationTime(input);

        if (auctionRepository.existsByItemIdAndAuctionStatusEnum(input.getItemId(), AuctionStatusEnum.ACTIVE) ||
                auctionRepository.existsByItemIdAndAuctionStatusEnum(input.getItemId(), AuctionStatusEnum.SCHEDULED)) {
            throw new ItemAlreadyInAuction("Current item already in active auction");
        }


        ZonedDateTime startTime =
                input.getStartingAt() != null
                        ? input.getStartingAt().withZoneSameInstant(BUSINESS_ZONE)
                        : now;

        AuctionStatusEnum initialStatus =
                startTime.isAfter(now)
                        ? AuctionStatusEnum.SCHEDULED
                        : AuctionStatusEnum.ACTIVE;

        Duration auctionDuration =
                (input.getDuration() != null)
                        ? input.getDuration()
                        : Duration.ofDays(1);


        Auction auction = new Auction();
        auction.setItem(Item);
        auction.setStartingAt(startTime);
        auction.setReservePrice(input.getReservePrice());
        auction.setStartingPrice(input.getStartingPrice());
        auction.setMinimumIncrement(input.getMinimumIncrement());
        auction.setAuctionDuration(auctionDuration);
        auction.setAuctionStatusEnum(initialStatus);

        auctionRepository.save(auction);

        return modelMapper.map(auction, ExposeAuctionDTO.class);
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

    public void closeAuction(Auction auction) {
        if (auction.getAuctionStatusEnum() != AuctionStatusEnum.ACTIVE) {
            return;
        }

        Optional<Bid> highestBid = bidRepository.findTopByAuctionOrderByPriceDesc(auction);

        if (highestBid.isPresent()
                && highestBid.get().getPrice().compareTo(auction.getReservePrice()) >= 0) {

            User winner = highestBid.get().getUser();

            auction.setAuctionStatusEnum(AuctionStatusEnum.PENDING_PAYMENT);
            auction.setWinner(winner);
            auction.setWinnerBid(highestBid.get());
            auction.setPaymentDeadline(ZonedDateTime.now().plusMinutes(10));

            emailService.sendAuctionWonEmail(winner, auction);

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
            throw new AuctionNonWaitingPaymentException("Auction is not awaiting payment");
        }

        if (ZonedDateTime.now().isAfter(auction.getPaymentDeadline())) {
            throw new PaymentWindowExpiredException("Payment window expired");
        }

        if (!auction.getWinner().getId().equals(userId)) {
            throw new NonWinnerPaysAuctionException("Only winner can pay");
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

    public List<ExposeAuctionDTO> showAllAuctions(AuctionStatusEnum status,BigDecimal minPrice,ZonedDateTime endsBefore){

        if (status == null){
            status = AuctionStatusEnum.ACTIVE;
        }

        if (minPrice == null){
            minPrice = BigDecimal.ZERO;
        }

        if (endsBefore == null){
            endsBefore = ZonedDateTime.now().plus(Duration.ofDays(7));
        }

        List<Auction> auctions = auctionRepository.search(status,minPrice,endsBefore);


        return auctions
                .stream()
                .map(auction -> modelMapper.map(auction, ExposeAuctionDTO.class))
                .toList();
    }

    public ExposeAuctionDTO getAuctionById(Long auctionId){

        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new AuctionNotFound("Auction not found"));

        return modelMapper.map(auction,ExposeAuctionDTO.class);
    }

    public List<ExposeAuctionDTO> getPendingPaymentAuctions() {

        Long userId = extractUserId();

        List<Auction> auctions = auctionRepository
                .findByAuctionStatusEnumAndWinner_Id(
                        AuctionStatusEnum.PENDING_PAYMENT,
                        userId
                );

        return auctions.stream()
                .map(a -> modelMapper.map(a, ExposeAuctionDTO.class))
                .toList();
    }

}
