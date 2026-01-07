package BiddingSystem.BiddingSystemRepo;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
public class AuctionTest extends BaseTestClass {

    private Long itemId;

    @BeforeEach
    public void setUp() {

        User user = userRepository.findUserByEmail("user1@gmail.com").orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail("user1@gmail.com");
            newUser.setUsername("user1");
            newUser.setPassword(passwordEncoder.encode("password"));
            return userRepository.save(newUser);
        });

        Item item = new Item();
        item.setName("Some Item");
        item.setDescription("Really cool item");
        item.setItemCategoryEnum(ItemCategoryEnum.COLLECTIBLES);
        item.setItemConditionEnum(ItemConditionEnum.USED);
        item.setOwner(user);

        entityManager.merge(item);
        entityManager.merge(user);

        itemRepository.save(item);
        itemId = item.getId();

        Assertions.assertNotNull(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(),
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void addingItemToAuction_shouldRunSuccessfully_whenValidInputAndDateTimeNow() throws Exception {

        ZonedDateTime requestTime = ZonedDateTime.now();
        Duration duration = Duration.ofMinutes(50L);

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "itemId": %d,
                                                "startingAt": "%s",
                                                "auctionDuration": "%s",
                                                "startingPrice": 10.00,
                                                "reservePrice": 20.00
                                            }
                                        """
                                        .formatted(itemId,
                                                requestTime.toString(),
                                                duration.toString()
                                        )
                                )
                )
                .andExpect(status().is2xxSuccessful());

        Auction auction = auctionRepository.findByItemId(itemId).orElseThrow(() -> new AssertionError("Auction not created"));

        Assertions.assertEquals(auction.getItem().getId(), itemId);

        Assertions.assertEquals(requestTime.toInstant(),
                auction.getStartingAt().toInstant());

        Assertions.assertEquals(AuctionStatusEnum.ACTIVE, auction.getAuctionStatusEnum());

        Assertions.assertEquals(requestTime.plus(duration).toInstant(), auction.getEndsAt().toInstant());

    }

    @Test
    public void addingItemToAuction_shouldRunSuccessfully_whenNotPassedStartTime() throws Exception {

        ZonedDateTime requestTime = ZonedDateTime.now();
        Duration duration = Duration.ofMinutes(50L);

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "itemId": %d,
                                                "startingAt": "%s",
                                                "auctionDuration": "%s",
                                                "startingPrice": 10.00,
                                                "reservePrice": 20.00
                                            }
                                        """
                                        .formatted(itemId,
                                                requestTime.toString(),
                                                duration.toString()
                                        )
                                )
                )
                .andExpect(status().is2xxSuccessful());

        Auction auction = auctionRepository.findByItemId(itemId).orElseThrow(() -> new AssertionError("Auction not created"));

        Assertions.assertEquals(auction.getItem().getId(), itemId);

        Assertions.assertEquals(requestTime.toInstant(),
                auction.getStartingAt().toInstant());

        Assertions.assertEquals(AuctionStatusEnum.ACTIVE, auction.getAuctionStatusEnum());

        Assertions.assertEquals(requestTime.plus(duration).toInstant(), auction.getEndsAt().toInstant());

    }

    @Test
    public void addingItemToAuction_shouldRunSuccessfully_whenValidInputAndDateInFuture() throws Exception {

        ZonedDateTime requestTime = ZonedDateTime.now().plusSeconds(1);
        Duration duration = Duration.ofMinutes(50L);

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "itemId": %d,
                                                "startingAt": "%s",
                                                "auctionDuration": "%s",
                                                "startingPrice": 10.00,
                                                "reservePrice": 20.00
                                            }
                                        """
                                        .formatted(itemId
                                                , requestTime.toString()
                                                , duration.toString()
                                        )
                                )
                )
                .andExpect(status().is2xxSuccessful());

        Auction auction = auctionRepository.findByItemId(itemId).orElseThrow(() -> new AssertionError("Auction not created"));

        Assertions.assertEquals(auction.getItem().getId(), itemId);

        Assertions.assertEquals(requestTime.toInstant(),
                auction.getStartingAt().toInstant());

        Assertions.assertEquals(AuctionStatusEnum.SCHEDULED, auction.getAuctionStatusEnum());

        Assertions.assertEquals(requestTime.plus(duration).toInstant(), auction.getEndsAt().toInstant());

    }

    @Test
    public void addingItemToAuction_shouldFail_whenReservePriceNegative() throws Exception {

        ZonedDateTime requestTime = ZonedDateTime.now();
        Duration duration = Duration.ofMinutes(50L);
        BigDecimal negativeReservePrice = new BigDecimal("-0.01");

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "itemId": %d,
                                                "startingAt": "%s",
                                                "auctionDuration": "%s",
                                                "startingPrice": 10.00,
                                                "reservePrice": %s
                                            }
                                        """
                                        .formatted(itemId,
                                                requestTime.toString(),
                                                duration.toString(),
                                                negativeReservePrice
                                        )
                                )
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addingItemToAuction_shouldFail_whenStartingPriceNegative() throws Exception {

        ZonedDateTime requestTime = ZonedDateTime.now();
        Duration duration = Duration.ofMinutes(50L);
        BigDecimal negativeStartingPrice = new BigDecimal("-0.01");

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "itemId": %d,
                                                "startingAt": "%s",
                                                "auctionDuration": "%s",
                                                "startingPrice": %s,
                                                "reservePrice": 20.00
                                            }
                                        """
                                        .formatted(itemId,
                                                requestTime.toString(),
                                                duration.toString(),
                                                negativeStartingPrice
                                        )
                                )
                )
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void addingItemToAuction_shouldFail_whenDurationTooShort() throws Exception {

        ZonedDateTime requestTime = ZonedDateTime.now();
        Duration duration = Duration.ofMinutes(9L);

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "itemId": %d,
                                                "startingAt": "%s",
                                                "auctionDuration": "%s",
                                                "startingPrice": 10.00,
                                                "reservePrice": 20.00
                                            }
                                        """
                                        .formatted(itemId,
                                                requestTime.toString(),
                                                duration.toString()
                                        )
                                )
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void addingItemToAuction_shouldFail_whenDurationTooLong() throws Exception {

        ZonedDateTime requestTime = ZonedDateTime.now();
        Duration duration = Duration.ofDays(7).plus(Duration.ofSeconds(1));

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                "itemId": %d,
                                                "startingAt": "%s",
                                                "auctionDuration": "%s",
                                                "startingPrice": 10.00,
                                                "reservePrice": 20.00
                                            }
                                        """
                                        .formatted(itemId,
                                                requestTime.toString(),
                                                duration.toString()
                                        )
                                )
                )
                .andExpect(status().is4xxClientError());
    }
}


