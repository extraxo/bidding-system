package BiddingSystem.BiddingSystemRepo.Repository;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long> {

    boolean existsByItemIdAndOwnerIdAndAuctionStatusEnum(Long itemId,Long ownerId, AuctionStatusEnum auctionStatusEnum);
}
