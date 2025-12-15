package BiddingSystem.BiddingSystemRepo.config;

import BiddingSystem.BiddingSystemRepo.Model.Entity.User;

import java.util.Map;

public interface JwtGeneratorInterface {
    Map<String, String> generateToken(User user);
}
