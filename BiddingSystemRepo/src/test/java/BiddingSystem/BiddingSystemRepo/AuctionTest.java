package BiddingSystem.BiddingSystemRepo;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
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

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: TASKS



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
        System.out.println(itemId);

        Assertions.assertNotNull(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(),
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

//    @Rollback(false)
    @Test
    void addingItemToAuction_shouldRun_Successfully() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "itemId": %d,
                                              "draft": true
                                            }
                                        """
                                        .formatted(itemId))
                )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void addingItemToAuction_shouldFail_whenItemWithNoSuchId() throws Exception {

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "itemId": 9999,
                                              "draft": false
                                            }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addingItemToAuction_shouldFail_whenSameItemAddedToMoreThanOneActiveAuctions() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "itemId": %d,
                                              "draft": true
                                            }
                                        """
                                        .formatted(itemId))
                )
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "itemId": %d,
                                              "draft": true
                                            }
                                        """
                                        .formatted(itemId))
                )
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "itemId": %d,
                                              "draft": false
                                            }
                                        """
                                        .formatted(itemId))
                )
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(
                        post("/api/v1/auction/addAuction")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "itemId": %d,
                                              "draft": false
                                            }
                                        """
                                        .formatted(itemId))
                )
                .andExpect(status().is4xxClientError());
    }
}


