package BiddingSystem.BiddingSystemRepo.Model.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class SystemBalance {

    @Id
    private Long id = 1L;

    private BigDecimal accumulatedDepositFees = BigDecimal.ZERO;
    private BigDecimal accumulatedWithdrawFees = BigDecimal.ZERO;

    public void updateFee(BigDecimal fee, boolean isDeposit) {
        if (isDeposit) {
            this.accumulatedDepositFees = this.accumulatedDepositFees.add(fee);
        } else {
            this.accumulatedWithdrawFees = this.accumulatedWithdrawFees.add(fee);
        }
    }

}
