package BiddingSystem.BiddingSystemRepo;


import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: Test token expire

public class AuthIntegrationTest extends BaseTestClass{

    @Test
    public void testAuthFunctionality() throws Exception {

        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username" : "uniqueUsername",
                                          "age": 30,
                                          "email": "newEmail99@abv.bg",
                                          "password": "newSecurePassword123",
                                          "address" : "groove Streer"
                                        }
                                        """)
                )
                .andExpect(status().is2xxSuccessful());

        String loginResponse = mockMvc.perform(
                        post("/api/v1/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "email": "newEmail99@abv.bg",
                                          "password": "newSecurePassword123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(loginResponse, "$.token");
        mockMvc.perform(
                        get("/api/v1/user/getRestrictedMaterial")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/v1/user/logout")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/v1/user/getRestrictedMaterial")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().is4xxClientError());
    }


//    @Test
//    public void logout_shouldFail_whenTokenIsInvalid() throws Exception {
//        mockMvc.perform(
//                        post("/api/v1/user/logout")
//                                .header("Authorization", "Bearer invalid-token")
//                )
//                .andExpect(status().is4xxClientError());
//    }

//    new change

}
