package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.DTO.SystemBalanceDTO.SystemBalanceResponseDTO;
import BiddingSystem.BiddingSystemRepo.Exception.SystemBalanceExceptions.SystemNotFoundException;
import BiddingSystem.BiddingSystemRepo.Model.Entity.SystemBalance;
import BiddingSystem.BiddingSystemRepo.Repository.SystemBalanceRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemBalanceService {

    private final SystemBalanceRepository systemBalanceRepository;

    public SystemBalanceService(SystemBalanceRepository systemBalanceRepository){
        this.systemBalanceRepository = systemBalanceRepository;
    }

    public SystemBalanceResponseDTO getSystemBalance(){
        SystemBalance systemBalance = systemBalanceRepository.findById(1L)
                .orElseThrow(() -> new SystemNotFoundException("System not found!"));

        return new SystemBalanceResponseDTO(
                systemBalance.getAccumulatedDepositFees(),
                systemBalance.getAccumulatedWithdrawFees()
        );
    }

}
