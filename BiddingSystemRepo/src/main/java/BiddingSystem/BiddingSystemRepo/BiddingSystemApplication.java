package BiddingSystem.BiddingSystemRepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BiddingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(BiddingSystemApplication.class, args);
    }
}

//FUNCTIONAL
//such as
// the search (user could search active auction based on their interest),
// payment (winner need to make the payment) and
// inventory (owner could add new items)
//User could start an auction
//User could view the active auction, and place a bid in the auction; user could also get realtime update on the current highest bid
//Auction is closed when there is no higher bid for 1 hour
//Winner of the auction would receive notification and has 10 minutes to make the payment


//
//Non Functional Requirements
//High availability
//High scalability
//Low latency
//Eventual consistency is acceptable for live bidding part (we could discuss for higher consistency level), but when determine the winner of the auction, it needs strong consistency
//1B DAU, 100k auctions per-day, on average 10% of user place 1 bid per day, assume 10:1 read:write ratio 💀💀💀