package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.Exception.UserBalanceException.NegativeMoneyAmountException;
import BiddingSystem.BiddingSystemRepo.Exception.UserExceptions.UserNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//TODO: Commission fee

@Service
public class UserBalanceService {

    private final BigDecimal PLATFORM_DEPOSIT_FEE = BigDecimal.valueOf(0.01);
    private final BigDecimal PLATFORM_WITHDRAW_FEE = BigDecimal.valueOf(0.05);

    private final UserRepository userRepository;

    public UserBalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deposit(Long userId, Long amount) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (amount < 0){
            throw new NegativeMoneyAmountException("Negative amount of deposit!");
        }

        BigDecimal currentBalance = user.getBalance();
        BigDecimal balanceToBeSet = currentBalance.add(BigDecimal.valueOf(amount));
        user.setBalance(balanceToBeSet);
        userRepository.save(user);

    }

    public void withdraw(Long userId, Long amount) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (amount < 0){
            throw new NegativeMoneyAmountException("Negative amount of deposit!");
        }

        BigDecimal currentBalance = user.getBalance();

//        TODO: Change with right exception type
        if (currentBalance.compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new NegativeMoneyAmountException("Insufficient funds for withdrawal!");
        }

        BigDecimal balanceToBeSet = currentBalance.subtract(BigDecimal.valueOf(amount));
        user.setBalance(balanceToBeSet);
        userRepository.save(user);


    }

    public BigDecimal viewBalance(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));
        return user.getBalance();

    }


}
