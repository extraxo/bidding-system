package BiddingSystem.BiddingSystemRepo.DTO.ItemDTO;

import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutputItemDTO {

    private Long id;

    private String name;

    private String description;

    private ItemCategoryEnum itemCategoryEnum;

    private ItemConditionEnum itemConditionEnum;
}
