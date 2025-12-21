package BiddingSystem.BiddingSystemRepo.config;

import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import BiddingSystem.BiddingSystemRepo.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {


    @Bean
    CommandLineRunner initDatabase(
    ) {
        return args -> {


        };
    }
}
