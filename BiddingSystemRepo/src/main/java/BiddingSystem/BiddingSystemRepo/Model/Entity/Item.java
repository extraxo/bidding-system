package BiddingSystem.BiddingSystemRepo.Model.Entity;


import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ItemCategoryEnum itemCategoryEnum;

    @Enumerated(EnumType.STRING)
    private ItemConditionEnum itemConditionEnum;

//    CHANGED FROM PERSIST FOR THE TESTS
    @ManyToOne(cascade = CascadeType.ALL)
    private User owner;

}
