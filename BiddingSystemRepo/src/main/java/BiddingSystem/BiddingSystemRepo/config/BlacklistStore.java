package BiddingSystem.BiddingSystemRepo.config;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class BlacklistStore {

    private Set<String> blacklistedTokens = new HashSet<>();

    public void addToken(String jwi) {
        blacklistedTokens.add(jwi);
    }

    public boolean contains(String jwi) {
        return blacklistedTokens.contains(jwi);
    }

}
