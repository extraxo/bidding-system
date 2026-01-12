package BiddingSystem.BiddingSystemRepo.DTO.SystemBalanceDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class SystemBalanceResponseDTO {

    private BigDecimal accumulatedDepositFees;
    private BigDecimal accumulatedWithdrawFees;

}
