package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.Service.AuctionService;
import BiddingSystem.BiddingSystemRepo.config.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuctionPaymentController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtFilter.class
                )
        }
)
@AutoConfigureMockMvc
class AuctionPaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuctionService auctionService;

    @Test
    @WithMockUser
    void shouldMakeAuctionPaymentSuccessfully() throws Exception {

        doNothing().when(auctionService).makePayment(1L);

        mockMvc.perform(post("/api/v1/auction/payment/{auctionId}", 1L)
                        .with(csrf()) // ⭐ FIX
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment Successfully"));
    }
}
