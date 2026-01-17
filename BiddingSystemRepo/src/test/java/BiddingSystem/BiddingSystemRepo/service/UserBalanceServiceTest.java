package BiddingSystem.BiddingSystemRepo.service;

import BiddingSystem.BiddingSystemRepo.DTO.PaymentDTO.SuccessfulDepositWithdrawDTO;
import BiddingSystem.BiddingSystemRepo.Exception.SystemBalanceExceptions.SystemNotFoundException;
import BiddingSystem.BiddingSystemRepo.Exception.UserBalanceException.*;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.SystemBalance;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Entity.UserTransactions;
import BiddingSystem.BiddingSystemRepo.Model.Enum.TransactionType;
import BiddingSystem.BiddingSystemRepo.Repository.SystemBalanceRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserTransactionsRepository;
import BiddingSystem.BiddingSystemRepo.Service.UserBalanceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBalanceServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SystemBalanceRepository systemBalanceRepository;

    @Mock
    private UserTransactionsRepository userTransactionsRepository;

    @InjectMocks
    private UserBalanceService userBalanceService;

    private static final Long USER_ID = 1L;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ---------- HELPERS ----------

    private User createUser(BigDecimal balance) {
        User user = new User();
        user.setId(USER_ID);
        user.setBalance(balance);
        return user;
    }

    private SystemBalance createSystemBalance() {
        return new SystemBalance();
    }

    private void mockAuthenticatedUser(Long userId) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // ---------- DEPOSIT TESTS ----------

    @Test
    void deposit_shouldThrowUserNotFound() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userBalanceService.deposit(USER_ID, BigDecimal.TEN));
    }

    @Test
    void deposit_shouldThrowSystemNotFound() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(createUser(BigDecimal.ZERO)));

        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(SystemNotFoundException.class,
                () -> userBalanceService.deposit(USER_ID, BigDecimal.TEN));
    }

    @Test
    void deposit_shouldFailOnNegativeAmount() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(createUser(BigDecimal.ZERO)));

        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.of(createSystemBalance()));

        assertThrows(NegativeMoneyAmountException.class,
                () -> userBalanceService.deposit(USER_ID, BigDecimal.ZERO));
    }

    @Test
    void deposit_shouldFailWhenAmountExceedsMax() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(createUser(BigDecimal.ZERO)));

        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.of(createSystemBalance()));

        assertThrows(MaxDepositWithdrawAmountException.class,
                () -> userBalanceService.deposit(USER_ID, BigDecimal.valueOf(2000)));
    }

    @Test
    void deposit_shouldSucceed() {
        User user = createUser(BigDecimal.valueOf(100));
        SystemBalance systemBalance = createSystemBalance();

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.of(systemBalance));

        SuccessfulDepositWithdrawDTO result =
                userBalanceService.deposit(USER_ID, BigDecimal.valueOf(100));

        assertThat(result).isNotNull();
        assertThat(result.getFee()).isEqualByComparingTo("1.00");
        assertThat(user.getBalance()).isEqualByComparingTo("199.00");

        verify(userTransactionsRepository).save(any(UserTransactions.class));
    }

    // ---------- WITHDRAW TESTS ----------

    @Test
    void withdraw_shouldFailOnInsufficientFunds() {
        User user = createUser(BigDecimal.valueOf(50));

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.of(createSystemBalance()));

        assertThrows(InsufficientFundsException.class,
                () -> userBalanceService.withdraw(USER_ID, BigDecimal.valueOf(100)));
    }

    @Test
    void withdraw_shouldFailWhenAmountExceedsMax() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(createUser(BigDecimal.valueOf(500))));

        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.of(createSystemBalance()));

        assertThrows(MaxDepositWithdrawAmountException.class,
                () -> userBalanceService.withdraw(USER_ID, BigDecimal.valueOf(500)));
    }

    @Test
    void withdraw_shouldSucceed() {
        User user = createUser(BigDecimal.valueOf(500));
        SystemBalance systemBalance = createSystemBalance();

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.of(systemBalance));

        SuccessfulDepositWithdrawDTO result =
                userBalanceService.withdraw(USER_ID, BigDecimal.valueOf(100));

        assertThat(result).isNotNull();
        assertThat(result.getFee()).isEqualByComparingTo("5.00");
        assertThat(user.getBalance()).isEqualByComparingTo("395.00");

        verify(userTransactionsRepository).save(any(UserTransactions.class));
    }

    // ---------- VIEW BALANCE ----------

    @Test
    void viewBalance_shouldReturnBalance() {
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(createUser(BigDecimal.valueOf(300))));

        BigDecimal balance = userBalanceService.viewBalance(USER_ID);

        assertThat(balance).isEqualByComparingTo("300");
    }

    // ---------- TRANSACTIONS ----------

    @Test
    void getMyTransactions_shouldReturnUserTransactions() {
        mockAuthenticatedUser(USER_ID);

        when(userTransactionsRepository.findByUser_Id(USER_ID))
                .thenReturn(List.of(new UserTransactions()));

        List<UserTransactions> result =
                userBalanceService.getMyTransactions();

        assertThat(result).hasSize(1);
    }
}
