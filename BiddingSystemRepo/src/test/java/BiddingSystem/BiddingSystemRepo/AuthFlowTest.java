package BiddingSystem.BiddingSystemRepo;


import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: Test token expire

public class AuthFlowTest extends BaseTestClass{

    @Test
    public void testAuthFunctionality() throws Exception {

        String loginResponse = mockMvc.perform(
                        post("/api/v1/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "email": "kacoLudiq@abv.bg",
                                          "password": "ivoIstinata"
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

    @Test
    public void login_shouldFail_whenEmailIsInvalid() throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "invalid-email-format",
                                              "password": "ivoIstinata"
                                            }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void login_shouldFail_whenPassowordIsInvalid() throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "kacoLudiq@abv.bg",
                                              "password": "SomeValidPassword123!"
                                            }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void testRegisterFunctionality() throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "newEmail@abv.bg",
                                              "password": "validPasswordIG!",
                                              "age" : "36",
                                              "username": "real_username"
                                            }
                                        """)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void register_should_failWithInvalidAge() throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "newEmail@abv.bg",
                                              "password": "validPasswordIG!",
                                              "age" : "12",
                                              "username": "real_username"
                                            }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void register_should_failWithInvalidEmail() throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "someEmail.bg",
                                              "password": "validPasswordIG!",
                                              "age" : "30",
                                              "username": "real_username"
                                            }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void register_should_failWithRepetitiveEmail() throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "someEmail@gmail.com",
                                              "password": "validPasswordIG!",
                                              "age" : "36",
                                              "username": "real_username_1"
                                            }
                                        """)
                )
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "someEmail@gmail.com",
                                              "password": "validPasswordIG!",
                                              "age" : "36",
                                              "username": "real_username_2"
                                            }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void register_should_failWithRepetitiveUsername() throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "someEmailABC@gmail.com",
                                              "password": "validPasswordIG!",
                                              "age" : "36",
                                              "username": "real_username"
                                            }
                                        """)
                )
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "email": "someEmaiDCAl@gmail.com",
                                              "password": "validPasswordIG!",
                                              "age" : "26",
                                              "username": "real_username"
                                            }
                                        """)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void logout_shouldFail_whenTokenIsInvalid() throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/logout")
                                .header("Authorization", "Bearer invalid-token")
                )
                .andExpect(status().is4xxClientError());
    }


}
