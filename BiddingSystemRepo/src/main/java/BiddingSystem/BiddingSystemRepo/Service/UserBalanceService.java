package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.PaymentDTO.SuccessfulDepositWithdrawDTO;
import BiddingSystem.BiddingSystemRepo.Exception.SystemBalanceExceptions.SystemNotFoundException;
import BiddingSystem.BiddingSystemRepo.Exception.UserBalanceException.InsufficientFundsException;
import BiddingSystem.BiddingSystemRepo.Exception.UserBalanceException.MaxDepositWithdrawAmountException;
import BiddingSystem.BiddingSystemRepo.Exception.UserBalanceException.NegativeMoneyAmountException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.SystemBalance;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Model.Entity.UserTransactions;
import BiddingSystem.BiddingSystemRepo.Model.Enum.TransactionType;
import BiddingSystem.BiddingSystemRepo.Repository.SystemBalanceRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import BiddingSystem.BiddingSystemRepo.Repository.UserTransactionsRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserBalanceService {

    private final BigDecimal PLATFORM_DEPOSIT_FEE = BigDecimal.valueOf(0.01);
    private final BigDecimal PLATFORM_WITHDRAW_FEE = BigDecimal.valueOf(0.05);
    private final BigDecimal MAX_SINGLE_DEPOSIT_VALUE = BigDecimal.valueOf(1000);
    private final BigDecimal MAX_SINGLE_WITHDRAW_VALUE = BigDecimal.valueOf(200);

    private final UserRepository userRepository;
    private final SystemBalanceRepository systemBalanceRepository;
    private final UserTransactionsRepository userTransactionsRepository;


    public UserBalanceService(UserRepository userRepository, SystemBalanceRepository systemBalanceRepository, UserTransactionsRepository userTransactionsRepository) {
        this.userRepository = userRepository;
        this.systemBalanceRepository = systemBalanceRepository;
        this.userTransactionsRepository = userTransactionsRepository;
    }

    private void validateAmount(BigDecimal amount, BigDecimal maxAmount, boolean isDeposit) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeMoneyAmountException("Amount must be greater than zero!");
        }

        if (amount.compareTo(maxAmount) > 0) {
            throw new MaxDepositWithdrawAmountException(
                    (isDeposit ? "Deposit" : "Withdraw") + " cap exceeded. Max: " + maxAmount
            );
        }
    }

    private BigDecimal applyFee(BigDecimal amount, boolean isDeposit, SystemBalance systemBalance) {

        BigDecimal feeRate = isDeposit ? PLATFORM_DEPOSIT_FEE : PLATFORM_WITHDRAW_FEE;
        BigDecimal fee = feeRate.multiply(amount);

        systemBalance.updateFee(fee, isDeposit);

        return fee;
    }

    public SuccessfulDepositWithdrawDTO deposit(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));
        SystemBalance systemBalance = systemBalanceRepository.findById(1L).orElseThrow(() -> new SystemNotFoundException("System not found!"));

        validateAmount(amount, MAX_SINGLE_DEPOSIT_VALUE, true);

        BigDecimal currentBalance = user.getBalance();
        BigDecimal balanceBeforeFee = currentBalance.add(amount);
        BigDecimal depositFee = applyFee(amount, true, systemBalance);

        BigDecimal balanceToBeSet = currentBalance.add(amount.subtract(depositFee));
        user.setBalance(balanceToBeSet);
        userRepository.save(user);

        UserTransactions tx = new UserTransactions();
        tx.setUser(user);
        tx.setTransactionType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setFee(depositFee);
        tx.setBalanceBefore(balanceBeforeFee);
        tx.setBalanceAfter(balanceToBeSet);

        userTransactionsRepository.save(tx);

        return new SuccessfulDepositWithdrawDTO(
                balanceBeforeFee,
                balanceToBeSet,
                depositFee,
                "Money Deposited Successfully."
        );
    }

    public SuccessfulDepositWithdrawDTO withdraw(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));
        SystemBalance systemBalance = systemBalanceRepository.findById(1L).orElseThrow(() -> new SystemNotFoundException("System not found!"));


        validateAmount(amount, MAX_SINGLE_WITHDRAW_VALUE, false);

        BigDecimal currentBalance = user.getBalance();


        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal!");
        }

        BigDecimal withdrawFee = applyFee(amount, false, systemBalance);

        BigDecimal balanceBeforeFee = currentBalance.subtract(amount);
        BigDecimal balanceToBeSet = currentBalance.subtract(amount).subtract(withdrawFee);
        user.setBalance(balanceToBeSet);
        userRepository.save(user);

        UserTransactions tx = new UserTransactions();
        tx.setUser(user);
        tx.setTransactionType(TransactionType.WITHDRAW);
        tx.setAmount(amount);
        tx.setFee(withdrawFee);
        tx.setBalanceBefore(balanceBeforeFee);
        tx.setBalanceAfter(balanceToBeSet);

        userTransactionsRepository.save(tx);

        return new SuccessfulDepositWithdrawDTO(
                balanceBeforeFee,
                balanceToBeSet,
                withdrawFee,
                "Money Withdrawn Successfully."
        );
    }

    public BigDecimal viewBalance(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));
        return user.getBalance();

    }

    public List<UserTransactions> getMyTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        return userTransactionsRepository.findByUser_Id(userId);
    }




}
