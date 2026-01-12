package BiddingSystem.BiddingSystemRepo.Repository;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long> {

    boolean existsByItemIdAndAuctionStatusEnum(Long itemId, AuctionStatusEnum auctionStatusEnum);

    List<Auction> findByAuctionStatusEnumAndEndsAtBefore(AuctionStatusEnum auctionStatusEnum, ZonedDateTime zonedDateTime);

    List<Auction> findByAuctionStatusEnumAndStartingAtLessThanEqual(AuctionStatusEnum auctionStatusEnum, ZonedDateTime startingAt);

    List<Auction> findByAuctionStatusEnumAndPaymentDeadlineBefore(AuctionStatusEnum status, ZonedDateTime time);

    @Query("""
    SELECT a
    FROM Auction a
    WHERE a.auctionStatusEnum = :status
      AND a.startingPrice >= :minPrice
      AND a.endsAt <= :endsBefore
""")
    List<Auction> search(
            @Param("status") AuctionStatusEnum status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("endsBefore") ZonedDateTime endsBefore
    );
}
