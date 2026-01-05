package BiddingSystem.BiddingSystemRepo.config;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import BiddingSystem.BiddingSystemRepo.Repository.AuctionRepository;
import BiddingSystem.BiddingSystemRepo.Repository.ItemRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, AuctionRepository auctionRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.itemRepository = itemRepository;
        this.auctionRepository = auctionRepository;
    }

    @Bean
    CommandLineRunner initDatabase(
    ) {
        return args -> {

            User user1 = new User();
            user1.setEmail("user1@gmail.com");
            user1.setUsername("theBigFish");
            user1.setPassword(passwordEncoder.encode("StrongPass123"));
            user1.setAge(30);
            user1.setAddress("Grove Street");


            User user2 = new User();
            user2.setEmail("user2@gmail.com");
            user2.setUsername("theSmallFish");
            user2.setPassword(passwordEncoder.encode("WeakPass987"));
            user2.setAge(33);
            user2.setAddress("Mega street");


            userRepository.save(user1);
            userRepository.save(user2);

            Item item = new Item();
            item.setName("Pot of Greed");
            item.setDescription("This is the pot of greed from Yu-Gi-Oh");
            item.setItemConditionEnum(ItemConditionEnum.NEW);
            item.setItemCategoryEnum(ItemCategoryEnum.COLLECTIBLES);
            item.setOwner(user1);

            Item item2 = new Item();
            item2.setName("Yu Gi Oh card");
            item2.setDescription("this card has special powers");
            item2.setItemConditionEnum(ItemConditionEnum.NEW);
            item2.setItemCategoryEnum(ItemCategoryEnum.COLLECTIBLES);
            item2.setOwner(user1);

            itemRepository.save(item);

            itemRepository.save(item2);

            Duration duration = Duration.ofMinutes(50);
            BigDecimal startPrice = new BigDecimal("10.00");
            BigDecimal reservePrice = new BigDecimal("10.00");

            Auction auction = new Auction();
            auction.setItem(item);
            auction.setStartingAt(ZonedDateTime.now());
            auction.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
            auction.setAuctionDuration(duration);
            auction.setStartingPrice(startPrice);
            auction.setReservePrice(reservePrice);

            auctionRepository.save(auction);


            Auction auction1 = new Auction();
            auction1.setStartingAt(ZonedDateTime.now());
            auction1.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
            auction1.setAuctionDuration(Duration.ofSeconds(10));

            Auction auction4 = new Auction();
            auction4.setStartingAt(ZonedDateTime.now());
            auction4.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
            auction4.setAuctionDuration(Duration.ofSeconds(15));

            Auction auction2 = new Auction();
            auction2.setStartingAt(ZonedDateTime.now());
            auction2.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
            auction2.setAuctionDuration(Duration.ofSeconds(20));

            Auction auction3 = new Auction();
            auction3.setStartingAt(ZonedDateTime.now());
            auction3.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
            auction3.setAuctionDuration(Duration.ofSeconds(19));

            List<Auction> auctionList = new ArrayList<>(Arrays.asList(auction1,auction2,auction3,auction4));
            auctionRepository.saveAll(auctionList);
        };
    }
}
