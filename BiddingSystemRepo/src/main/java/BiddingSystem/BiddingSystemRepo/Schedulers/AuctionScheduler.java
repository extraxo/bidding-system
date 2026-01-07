package BiddingSystem.BiddingSystemRepo.Schedulers;

import BiddingSystem.BiddingSystemRepo.Service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//TODO: Scheduler for auto opening of auctions ( DRAFT -> ACTIVE)

@Service
public class AuctionScheduler {

    private final AuctionService auctionService;

    public AuctionScheduler(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // Bidding time ends
    @Scheduled(initialDelay = 10000, fixedDelay = 60000)
    public void checkForExpiredAuctions() {
        auctionService.finishExpiredAuctions();
    }

    // Payment time ends
    @Scheduled(fixedDelay = 3000)
    public void checkForUnpaidAuctions() {
        auctionService.finishUnpaidAuctions();
    }

    @Scheduled(fixedDelay = 3000)
    public void checkForScheduledAuctions() {
        auctionService.makeAuctionActive();
    }

}
