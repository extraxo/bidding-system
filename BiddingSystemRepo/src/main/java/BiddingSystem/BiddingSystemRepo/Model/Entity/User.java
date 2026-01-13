package BiddingSystem.BiddingSystemRepo.Model.Entity;

import BiddingSystem.BiddingSystemRepo.Model.Enum.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
//TODO: User ban/block list
// TODO: Maximum active auctions
public class User extends BaseEntity {

    @NotBlank
    @Column(unique = true,nullable = false)
    private String username;

    @Min(18)
    @Max(80)
    @Column(nullable = false)
    private int age;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false)
    private String address;

    private BigDecimal balance = BigDecimal.valueOf(0.0);

    @Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.BaseUser;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Set<Item> itemSet = new HashSet<>();

}
