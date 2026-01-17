package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.PaymentDTO.SuccessfulDepositWithdrawDTO;
import BiddingSystem.BiddingSystemRepo.Model.Entity.UserTransactions;
import BiddingSystem.BiddingSystemRepo.Service.UserBalanceService;
import BiddingSystem.BiddingSystemRepo.config.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserBalanceController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class UserBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserBalanceService userBalanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupSecurityContext() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(1L);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    // --------------------------------
    // POST /api/v1/user/balance/deposit
    // --------------------------------
    @Test
    void shouldDepositSuccessfully() throws Exception {

        SuccessfulDepositWithdrawDTO response =
                new SuccessfulDepositWithdrawDTO(
                        BigDecimal.valueOf(100),   // before fee
                        BigDecimal.valueOf(98),    // after fee
                        BigDecimal.valueOf(2),     // fee
                        "Deposit successful"
                );

        Mockito.when(userBalanceService.deposit(any(Long.class), any(BigDecimal.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/user/balance/deposit")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalanceBeforeFee").value(100))
                .andExpect(jsonPath("$.newBalanceAfterFee").value(98))
                .andExpect(jsonPath("$.fee").value(2))
                .andExpect(jsonPath("$.message").value("Deposit successful"));
    }

    // --------------------------------
    // POST /api/v1/user/balance/withdraw
    // --------------------------------
    @Test
    void shouldWithdrawSuccessfully() throws Exception {

        SuccessfulDepositWithdrawDTO response =
                new SuccessfulDepositWithdrawDTO(
                        BigDecimal.valueOf(200),
                        BigDecimal.valueOf(180),
                        BigDecimal.valueOf(20),
                        "Withdraw successful"
                );

        Mockito.when(userBalanceService.withdraw(any(Long.class), any(BigDecimal.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/user/balance/withdraw")
                        .param("amount", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalanceBeforeFee").value(200))
                .andExpect(jsonPath("$.newBalanceAfterFee").value(180))
                .andExpect(jsonPath("$.fee").value(20))
                .andExpect(jsonPath("$.message").value("Withdraw successful"));
    }

    // --------------------------------
    // GET /api/v1/user/balance
    // --------------------------------
    @Test
    void shouldViewBalanceSuccessfully() throws Exception {

        Mockito.when(userBalanceService.viewBalance(any(Long.class)))
                .thenReturn(BigDecimal.valueOf(250));

        mockMvc.perform(get("/api/v1/user/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("User balance: 250"));
    }

    // --------------------------------
    // GET /api/v1/user/balance/transactions
    // --------------------------------
    @Test
    void shouldReturnUserTransactions() throws Exception {

        UserTransactions tx = Mockito.mock(UserTransactions.class);

        Mockito.when(userBalanceService.getMyTransactions())
                .thenReturn(List.of(tx));

        mockMvc.perform(get("/api/v1/user/balance/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

}
