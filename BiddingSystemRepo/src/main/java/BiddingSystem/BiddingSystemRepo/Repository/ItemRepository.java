package BiddingSystem.BiddingSystemRepo.Repository;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByOwnerAndName(User user, String name);

    Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);
}
