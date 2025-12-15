package BiddingSystem.BiddingSystemRepo.Model.Entity;


import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item")
public class Item extends BaseEntity {

    private String name;

    @Enumerated(EnumType.STRING)
    private ItemCategoryEnum itemCategoryEnum;

    @Enumerated(EnumType.STRING)
    private ItemConditionEnum itemConditionEnum;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User owner;

}
