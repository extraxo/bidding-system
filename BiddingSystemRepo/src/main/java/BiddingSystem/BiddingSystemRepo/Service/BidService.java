package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.CreateBidInput;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.AuctionBidOnInvalidStatus;
import BiddingSystem.BiddingSystemRepo.Exception.AuctionException.AuctionNotFound;
import BiddingSystem.BiddingSystemRepo.Exception.BidException.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository, UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    public void makeBid(CreateBidInput createBidInput) {

        ZonedDateTime now = ZonedDateTime.now();

        Auction auction = auctionRepository.findById(createBidInput.getAuctionId())
                .orElseThrow(() -> new AuctionNotFound("Auction with this id not found!"));

        if (auction.getAuctionStatusEnum() != AuctionStatusEnum.ACTIVE) {
            throw new AuctionBidOnInvalidStatus("You can bid only on active auctions");
        }

        if (auction.getEndsAt().isBefore(now)) {
            throw new BidSentAfterEndTimeException("Bid cannot be sent after end of auction");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Such user not found"));

        if (Objects.equals(auction.getItem().getOwner().getId(), userId)) {
            throw new OwnerBiddingOwnItemException("Owner of item cannot bid on its own listed items!");
        }

        BigDecimal currentPrice = bidRepository
                .findTopByAuctionOrderByPriceDesc(auction)
                .map(Bid::getPrice)
                .orElse(auction.getStartingPrice());

        if (currentPrice.compareTo(createBidInput.getBidPrice()) >= 0) {
            throw new InvalidBidException("New bid must be higher than the current price!");
        }

        if (createBidInput.getBidPrice().subtract(currentPrice).compareTo(auction.getMinimumIncrement()) < 0) {
            throw new InvalidBidIncrementException("New bid does not meet minimum increment requirements!");
        }

        Bid bid = new Bid();
        bid.setUser(user);
        bid.setAuction(auction);
        bid.setCreatedAt(now); 
        bid.setPrice(createBidInput.getBidPrice());

        bidRepository.save(bid);
    }
}
