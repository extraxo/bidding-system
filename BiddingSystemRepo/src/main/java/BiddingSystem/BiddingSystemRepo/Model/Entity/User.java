package BiddingSystem.BiddingSystemRepo.Model.Entity;

import BiddingSystem.BiddingSystemRepo.Model.Enum.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true)
    private String username;

    private int age;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.BaseUser;

    @JsonIgnore
    private String SECRET_INFORMATION = "THIS_STRING_SHOULD_NOT_BE_EXPOSED_EVER";

}
