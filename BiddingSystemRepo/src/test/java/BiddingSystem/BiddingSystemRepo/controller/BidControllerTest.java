package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.BidDTO;
import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.CreateBidDTO;
import BiddingSystem.BiddingSystemRepo.DTO.BidDTO.CreateBidInput;
import BiddingSystem.BiddingSystemRepo.Service.BidService;
import BiddingSystem.BiddingSystemRepo.config.BlacklistStore;
import BiddingSystem.BiddingSystemRepo.config.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BidController.class)
@AutoConfigureMockMvc(addFilters = false)
class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BidService bidService;

    // 🔥 ВАЖНО: mock-ваме security bean-овете
    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private BlacklistStore blacklistStore;

    @Test
    void shouldMakeBidSuccessfully() throws Exception {
        CreateBidDTO request = new CreateBidDTO();
        request.setAuctionId(1L);
        request.setBidPrice(BigDecimal.valueOf(100));

        BidDTO response = new BidDTO();
        response.setPrice(BigDecimal.valueOf(100));
        response.setCreatedAt(ZonedDateTime.now());

        Mockito.when(bidService.makeBid(any(CreateBidInput.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/bid/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(100));
    }
}

