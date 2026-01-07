package BiddingSystem.BiddingSystemRepo.Response.UserResponseDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateItemResponseDTO {

    private String name;

    private String description;
}
