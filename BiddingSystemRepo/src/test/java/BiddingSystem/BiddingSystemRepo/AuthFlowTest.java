package BiddingSystem.BiddingSystemRepo;


import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
// Give me Spring but for testing (set it up the same way)
@AutoConfigureMockMvc
// Create fake HTTP client that will send requests
public class AuthFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("kacoLudiq@abv.bg");
        user.setPassword(passwordEncoder.encode("ivoIstinata"));

        userRepository.save(user);
    }

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
