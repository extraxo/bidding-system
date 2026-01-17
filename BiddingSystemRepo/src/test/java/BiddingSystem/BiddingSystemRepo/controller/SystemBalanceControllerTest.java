package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.SystemBalanceDTO.SystemBalanceResponseDTO;
import BiddingSystem.BiddingSystemRepo.Service.SystemBalanceService;
import BiddingSystem.BiddingSystemRepo.config.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SystemBalanceController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class SystemBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SystemBalanceService systemBalanceService;

    @Autowired
    private ObjectMapper objectMapper;

    // --------------------------------
    // GET /api/v1/systemBalance/
    // --------------------------------
    @Test
    void shouldReturnSystemBalanceSuccessfully() throws Exception {

        SystemBalanceResponseDTO response =
                new SystemBalanceResponseDTO(
                        BigDecimal.valueOf(120.50),
                        BigDecimal.valueOf(30.00)
                );

        Mockito.when(systemBalanceService.getSystemBalance())
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/systemBalance/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accumulatedDepositFees").value(120.50))
                .andExpect(jsonPath("$.accumulatedWithdrawFees").value(30.00));
    }
}
