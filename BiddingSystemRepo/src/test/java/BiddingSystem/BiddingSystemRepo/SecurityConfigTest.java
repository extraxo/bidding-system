package BiddingSystem.BiddingSystemRepo;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.function.Predicate.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void publicEndpoint_doesNotRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "someemail@abv.bg",
                                    "password": "hugePass123+"
                                }
                                """))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void privateEndpoint_requiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/item/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "someName",
                                    "description": "SomeDescription",
                                    "itemCategoryEnum": "ELECTRONICS",
                                    "itemConditionEnum": "NEW"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
