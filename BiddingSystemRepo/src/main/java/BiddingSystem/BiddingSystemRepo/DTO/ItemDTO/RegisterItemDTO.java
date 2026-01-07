package BiddingSystem.BiddingSystemRepo.DTO.ItemDTO;

import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class RegisterItemDTO {

    @NotBlank(message = "Item name cannot be blank!")
    private String name;

    @Size(min = 2, message = "Please add more descriptive text!")
    @Size(max = 200, message = "Description too long!")
    private String description;

    @NotNull(message = "Category is required")
    private ItemCategoryEnum itemCategoryEnum;

    @NotNull(message = "Condition is required")
    private ItemConditionEnum itemConditionEnum;

    public void setName(String name) {
        this.name = name == null ? null : name.trim().toLowerCase();
    }
}
