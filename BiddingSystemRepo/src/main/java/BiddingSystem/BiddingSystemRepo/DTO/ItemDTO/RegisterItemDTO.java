package BiddingSystem.BiddingSystemRepo.DTO.ItemDTO;

import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegisterItemDTO {

    @NotBlank
    private String name;

    @Size(min = 2, message = "Please add more descriptive text!")
    @Size(max = 200, message = "Description too long!")
    private String description;

    private ItemCategoryEnum itemCategoryEnum;

    private ItemConditionEnum itemConditionEnum;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
