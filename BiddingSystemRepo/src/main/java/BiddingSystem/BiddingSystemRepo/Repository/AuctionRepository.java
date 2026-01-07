package BiddingSystem.BiddingSystemRepo.Repository;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long> {

    boolean existsByItemIdAndAuctionStatusEnum(Long itemId, AuctionStatusEnum auctionStatusEnum);

    List<Auction> findByAuctionStatusEnumAndEndsAtBefore(AuctionStatusEnum auctionStatusEnum, ZonedDateTime zonedDateTime);

    List<Auction> findByAuctionStatusEnumAndStartingAtLessThanEqual(AuctionStatusEnum auctionStatusEnum, ZonedDateTime startingAt);

    List<Auction> findByAuctionStatusEnumAndPaymentDeadlineBefore(AuctionStatusEnum status, ZonedDateTime time);

    Optional<Auction> findByItemId(Long itemId);
}
