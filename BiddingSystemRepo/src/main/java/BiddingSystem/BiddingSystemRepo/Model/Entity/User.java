package BiddingSystem.BiddingSystemRepo.Model.Entity;

import BiddingSystem.BiddingSystemRepo.Model.Enum.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
//TODO: User ban/block list
// TODO: Maximum active auctions
public class User extends BaseEntity {

    @Column(unique = true)
    private String username;

    private int age;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.BaseUser;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private String SECRET_INFORMATION = "THIS_STRING_SHOULD_NOT_BE_EXPOSED_EVER";

    @JsonIgnore
    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Item> itemSet = new HashSet<>();

}
