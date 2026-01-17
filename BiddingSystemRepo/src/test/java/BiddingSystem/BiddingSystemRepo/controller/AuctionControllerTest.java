package BiddingSystem.BiddingSystemRepo.controller;

import BiddingSystem.BiddingSystemRepo.Controller.AuctionController;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.AddItemToAuctionDTO;
import BiddingSystem.BiddingSystemRepo.DTO.AuctionDTO.ExposeAuctionDTO;
import BiddingSystem.BiddingSystemRepo.Model.Enum.AuctionStatusEnum;
import BiddingSystem.BiddingSystemRepo.Service.AuctionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(
        controllers = AuctionController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = BiddingSystem.BiddingSystemRepo.config.JwtFilter.class
                )
        }
)

@AutoConfigureMockMvc
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuctionService auctionService;

    @Autowired
    private ObjectMapper objectMapper;

    // -------------------------------------------------
    // POST /api/v1/auction/
    // -------------------------------------------------
    @Test
    void shouldCreateAuction() throws Exception {

        AddItemToAuctionDTO dto = new AddItemToAuctionDTO();
        dto.setItemId(1L);
        dto.setStartingAt(ZonedDateTime.now().plusMinutes(1));
        dto.setAuctionDuration(Duration.ofMinutes(30));
        dto.setStartingPrice(BigDecimal.valueOf(10));
        dto.setReservePrice(BigDecimal.valueOf(20));
        dto.setMinimumIncrement(BigDecimal.valueOf(1));

        ExposeAuctionDTO response = new ExposeAuctionDTO();
        response.setId(100L);

        when(auctionService.createAuction(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auction/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L));
    }

    // -------------------------------------------------
    // GET /api/v1/auction/
    // -------------------------------------------------
    @Test
    void shouldReturnAllAuctionsWithDefaults() throws Exception {

        when(auctionService.showAllAuctions(
                eq(AuctionStatusEnum.ACTIVE),
                eq(BigDecimal.ZERO),
                any(ZonedDateTime.class)))
                .thenReturn(List.of(new ExposeAuctionDTO()));

        mockMvc.perform(get("/api/v1/auction/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // -------------------------------------------------
    // GET /api/v1/auction/{id}
    // -------------------------------------------------
    @Test
    void shouldReturnAuctionById() throws Exception {

        ExposeAuctionDTO dto = new ExposeAuctionDTO();
        dto.setId(5L);

        when(auctionService.getAuctionById(5L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/auction/{auctionId}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    // -------------------------------------------------
    // GET /api/v1/auction/pendingPayment
    // -------------------------------------------------
    @Test
    void shouldReturnPendingPaymentAuctions() throws Exception {

        when(auctionService.getPendingPaymentAuctions())
                .thenReturn(List.of(new ExposeAuctionDTO()));

        mockMvc.perform(get("/api/v1/auction/pendingPayment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // -------------------------------------------------
    // VALIDATION TEST
    // -------------------------------------------------
    @Test
    void shouldFailValidationWhenBodyIsInvalid() throws Exception {

        AddItemToAuctionDTO invalidDto = new AddItemToAuctionDTO(); // празно тяло

        mockMvc.perform(post("/api/v1/auction/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
