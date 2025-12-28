package BiddingSystem.BiddingSystemRepo;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Item;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import com.jayway.jsonpath.JsonPath;
//import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// API Integration tests
@Transactional
public class ItemTest extends BaseTestClass {



    @BeforeEach
    public void setup() {

        Item item = new Item();
        item.setName("Picture2");
        item.setDescription("A beautiful painting.");
        item.setItemCategoryEnum(ItemCategoryEnum.ENTERTAINMENT_AND_MEDIA);
        item.setItemConditionEnum(ItemConditionEnum.VINTAGE);

        User user = userRepository.findUserByEmail("kacoLudiq@abv.bg").orElse(null);
        item.setOwner(user);
        user.getItemSet().add(item);

        // Save the item and the user in the same transaction
        entityManager.merge(user);
        entityManager.merge(item);

        Assertions.assertNotNull(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    @Test
    void addingItem_shouldFail_whenUserAddItemsWithSameName() throws Exception {
        mockMvc.perform(
                        post("/api/v1/item/addItem")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "name": "Picture3",
                                                    "description": "some of the best pictures of Mona Lisa",
                                                    "itemCategoryEnum": "ENTERTAINMENT_AND_MEDIA",
                                                    "itemConditionEnum": "VINTAGE"
                                                }
                                        """)
                )
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(
                        post("/api/v1/item/addItem")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "name": "Picture2",
                                                    "description": "best picturefff ever trust",
                                                    "itemCategoryEnum": "ENTERTAINMENT_AND_MEDIA",
                                                    "itemConditionEnum": "VINTAGE"
                                                }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addingItem_shouldFail_whenInvalidTokenAdded() throws Exception {
        mockMvc.perform(
                        post("/api/v1/item/addItem")
                                .header("Authorization", "Bearer " + "SomeToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "name": "Picture5",
                                                    "description": "best picturefff ever trust",
                                                    "itemCategoryEnum": "ENTERTAINMENT_AND_MEDIA",
                                                    "itemConditionEnum": "VINTAGE"
                                                }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addingItem_shouldFail_whenUserLogOutAndTryToAddItem() throws Exception {


        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(
                        post("/api/v1/item/addItem")
//                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "name": "Picture2",
                                                    "description": "best picturefff ever trust",
                                                    "itemCategoryEnum": "ENTERTAINMENT_AND_MEDIA",
                                                    "itemConditionEnum": "VINTAGE"
                                                }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

}
