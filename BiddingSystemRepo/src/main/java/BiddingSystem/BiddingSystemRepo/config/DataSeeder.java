package BiddingSystem.BiddingSystemRepo.config;

import BiddingSystem.BiddingSystemRepo.Model.Entity.*;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.RoleEnum;
import BiddingSystem.BiddingSystemRepo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private final BidRepository bidRepository;
    private final SystemBalanceRepository systemBalanceRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, AuctionRepository auctionRepository,
                      ItemRepository itemRepository, BidRepository bidRepository, SystemBalanceRepository systemBalanceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.itemRepository = itemRepository;
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.systemBalanceRepository = systemBalanceRepository;
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

            User user3 = new User();
            user3.setEmail("brotha@email.com");
            user3.setUsername("Cooking123+");
            user3.setPassword(passwordEncoder.encode("MyPassword"));
            user3.setAge(40);
            user3.setAddress("Home");

            User admin = new User();
            admin.setEmail("admin@abv.bg");
            admin.setUsername("Amin123+");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setAge(50);
            admin.setAddress("Site");
            admin.setRole(RoleEnum.Admin);

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(admin);

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

            Item item3 = new Item();
            item3.setName("SomeNew Item");
            item3.setDescription("Item of the universe");
            item3.setItemConditionEnum(ItemConditionEnum.NEW);
            item3.setItemCategoryEnum(ItemCategoryEnum.COLLECTIBLES);
            item3.setOwner(user2);

            itemRepository.save(item);
            itemRepository.save(item2);
            itemRepository.save(item3);


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
            auction1.setAuctionDuration(Duration.ofSeconds(300));
            auction1.setStartingPrice(startPrice);
            auction1.setMinimumIncrement(BigDecimal.valueOf(5));
            auction1.setItem(item3);


            Auction auction4 = new Auction();
            auction4.setStartingAt(ZonedDateTime.now());
            auction4.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
            auction4.setAuctionDuration(Duration.ofSeconds(300));
            auction4.setStartingPrice(startPrice);


            Auction auction2 = new Auction();
            auction2.setStartingAt(ZonedDateTime.now());
            auction2.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
            auction2.setAuctionDuration(Duration.ofSeconds(300));
            auction2.setStartingPrice(startPrice);

            Auction auction3 = new Auction();
            auction3.setStartingAt(ZonedDateTime.now());
            auction3.setAuctionStatusEnum(AuctionStatusEnum.ACTIVE);
            auction3.setAuctionDuration(Duration.ofSeconds(60));
            auction3.setStartingPrice(startPrice);
            auction3.setMinimumIncrement(BigDecimal.valueOf(10));
            auction3.setStartingPrice(BigDecimal.valueOf(35));
            auction3.setReservePrice(BigDecimal.valueOf(60));
            auction3.setItem(item3);

            List<Auction> auctionList = new ArrayList<>(Arrays.asList(auction1,auction2,auction3,auction4));
            auctionRepository.saveAll(auctionList);

            Bid bid1 = new Bid();
            bid1.setAuction(auction);
            bid1.setUser(user2);
            bid1.setPrice(new BigDecimal("12.50"));
            bid1.setCreatedAt(ZonedDateTime.now().minusMinutes(5));

            Bid bid2 = new Bid();
            bid2.setAuction(auction);
            bid2.setUser(user1);
            bid2.setPrice(new BigDecimal("15.00"));
            bid2.setCreatedAt(ZonedDateTime.now().minusMinutes(3));

            Bid bid3 = new Bid();
            bid3.setAuction(auction);
            bid3.setUser(user2);
            bid3.setPrice(new BigDecimal("18.00"));
            bid3.setCreatedAt(ZonedDateTime.now().minusMinutes(1));

            bidRepository.saveAll(List.of(bid1, bid2, bid3));

            SystemBalance systemBalance = new SystemBalance();
            systemBalanceRepository.save(systemBalance);
        };
    }
}
