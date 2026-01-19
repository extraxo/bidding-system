package BiddingSystem.BiddingSystemRepo.Service;

import BiddingSystem.BiddingSystemRepo.Model.Entity.Auction;
import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOutbidEmail(User outbidUser, Auction auction, BigDecimal newBidPrice) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@biddingsystem.com");
        message.setTo(outbidUser.getEmail());
        message.setSubject("You were outbid on " + auction.getItem().getName());

        message.setText(
                "Hello " + outbidUser.getUsername() + ",\n\n" +
                        "You have been outbid on the item:\n" +
                        auction.getItem().getName() + "\n\n" +
                        "New highest bid: " + newBidPrice + "\n\n" +
                        "Hurry up and place a higher bid if you still want to win!\n\n" +
                        "Best regards,\nBidding System"
        );

        mailSender.send(message);
    }

    public void sendAuctionWonEmail(User winner, Auction auction) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@biddingsystem.com");
        message.setTo(winner.getEmail());
        message.setSubject("You won the auction: " + auction.getItem().getName());

        message.setText(
                "Hello " + winner.getUsername() + ",\n\n" +
                        "Congratulations! 🎉\n\n" +
                        "You have won the auction for:\n" +
                        auction.getItem().getName() + "\n\n" +
                        "Winning bid: " + auction.getWinnerBid().getPrice() + "\n\n" +
                        "Please complete your payment before:\n" +
                        auction.getPaymentDeadline() + "\n\n" +
                        "Thank you for using our Bidding System."
        );

        mailSender.send(message);
    }


}

