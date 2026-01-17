package BiddingSystem.BiddingSystemRepo.Model.Entity;


import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {

    @NotBlank(message = "Item name cannot be blank!")
    private String name;

    @Size(min = 2, message = "Please add more descriptive text!")
    @Size(max = 200, message = "Description too long!")
    private String description;

    @Enumerated(EnumType.STRING)
    private ItemCategoryEnum itemCategoryEnum;

    @Enumerated(EnumType.STRING)
    private ItemConditionEnum itemConditionEnum;

    @ManyToOne()
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

}
