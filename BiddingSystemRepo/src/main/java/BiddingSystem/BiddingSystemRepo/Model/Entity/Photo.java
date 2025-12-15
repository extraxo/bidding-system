package BiddingSystem.BiddingSystemRepo.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "photo")
public class Photo extends BaseEntity {

    private String name;

    private String photoURL;
    //    TEST
    //    again cloudinary
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Item item;

}
