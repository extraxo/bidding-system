package BiddingSystem.BiddingSystemRepo.config;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;

//@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret; // Inject secret from application.properties

    @Bean
    public SecretKey secretKey() {
        // Convert the secret string to a SecretKey
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {

        // disable CSRF since we use fronted (any other port, not server side rendered
        // app)
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults()); // ✅ Enable the CORS config you defined

        // permit specific request (3), other authenticated (using our JWT filter)
        http.authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/api/v1/user/register",
                        "/api/v1/user/login",
                        "/swagger-ui/**",
                        "/swagger-ui.html", // Ensure this is also allowed
                        "/v3/api-docs", // Explicitly allow this path
                        "/v3/api-docs/**" // Allow all v3/api-docs paths
                )
                .permitAll()  // These paths don't need JWT authentication
                .requestMatchers("/api/v1/item/**").authenticated()
                .anyRequest().authenticated());

        // don't use sessions because again we use JWT
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


//        returns 401 unauthenticated
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                        (request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
        );
        // runs before the UsernamePassword filter
        // but since we added the user to trusted/authenticated users in the aut local
        // storage (see JwtFilter.java)
        // the logic of UsernamePasswordAuthenticationFilter is skipped since user
        // already trusted
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}