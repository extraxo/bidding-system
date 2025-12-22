package BiddingSystem.BiddingSystemRepo.config;


import BiddingSystem.BiddingSystemRepo.Model.Entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtGeneratorInterfaceImpl{

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.message}")
    private String message;

    private static final int TOKEN_EXPIRY_TIME_IN_MINUTES = 60;

    public Map<String, String> generateToken(User user) {

        String jti = UUID.randomUUID().toString();
        String jwtToken;
        jwtToken = Jwts.builder()
                .claims()
//               Begins adding claims (payload info).
                .id(jti)
//                subject is put into the token - ACTUAL INFO IN IT
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
//                24 minutes xD
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * TOKEN_EXPIRY_TIME_IN_MINUTES))
                .and()
//                end of jwt builder back to main one

                .signWith(getSignInKey())
                .compact();

        Map<String, String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        jwtTokenGen.put("message", message);

        return jwtTokenGen;
    }

    private Key getSignInKey(){
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}