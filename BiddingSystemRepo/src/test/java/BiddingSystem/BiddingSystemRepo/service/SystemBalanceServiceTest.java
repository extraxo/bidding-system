package BiddingSystem.BiddingSystemRepo.service;

import BiddingSystem.BiddingSystemRepo.DTO.SystemBalanceDTO.SystemBalanceResponseDTO;
import BiddingSystem.BiddingSystemRepo.Exception.SystemBalanceExceptions.SystemNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.SystemBalance;
import BiddingSystem.BiddingSystemRepo.Repository.SystemBalanceRepository;
import BiddingSystem.BiddingSystemRepo.Service.SystemBalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemBalanceServiceTest {

    @Mock
    private SystemBalanceRepository systemBalanceRepository;

    @InjectMocks
    private SystemBalanceService systemBalanceService;

    @Test
    void getSystemBalance_shouldReturnSystemBalanceDTO_whenSystemExists() {
        SystemBalance systemBalance = new SystemBalance();
        systemBalance.setAccumulatedDepositFees(BigDecimal.valueOf(100));
        systemBalance.setAccumulatedWithdrawFees(BigDecimal.valueOf(50));

        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.of(systemBalance));

        SystemBalanceResponseDTO result = systemBalanceService.getSystemBalance();

        assertThat(result).isNotNull();
        assertThat(result.getAccumulatedDepositFees()).isEqualByComparingTo("100");
        assertThat(result.getAccumulatedWithdrawFees()).isEqualByComparingTo("50");
    }

    @Test
    void getSystemBalance_shouldThrowException_whenSystemNotFound() {
        when(systemBalanceRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(SystemNotFoundException.class,
                () -> systemBalanceService.getSystemBalance());
    }
}
