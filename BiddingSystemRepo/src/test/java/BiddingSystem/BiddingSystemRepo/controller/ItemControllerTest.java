package BiddingSystem.BiddingSystemRepo.Controller;

import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.OutputItemDTO;
import BiddingSystem.BiddingSystemRepo.DTO.ItemDTO.RegisterItemDTO;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemCategoryEnum;
import BiddingSystem.BiddingSystemRepo.Model.Enum.ItemConditionEnum;
import BiddingSystem.BiddingSystemRepo.Service.ItemService;
import BiddingSystem.BiddingSystemRepo.config.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ItemController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    // --------------------------------
    // POST /api/v1/item/
    // --------------------------------
    @Test
    void shouldAddItemSuccessfully() throws Exception {

        RegisterItemDTO request = new RegisterItemDTO(
                "Laptop",
                "Gaming laptop",
                ItemCategoryEnum.ELECTRONICS,
                ItemConditionEnum.NEW
        );

        OutputItemDTO response = new OutputItemDTO();
        response.setName("laptop"); // lowercase заради setter-а
        response.setDescription("Gaming laptop");

        Mockito.when(itemService.addItem(any(RegisterItemDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/item/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("laptop"))
                .andExpect(jsonPath("$.description").value("Gaming laptop"));
    }

    // --------------------------------
    // POST /api/v1/item/ - validation
    // --------------------------------
    @Test
    void shouldFailValidationWhenRequestIsInvalid() throws Exception {

        RegisterItemDTO invalidRequest = new RegisterItemDTO(
                "",      // invalid name
                "a",     // too short
                null,    // missing category
                null     // missing condition
        );

        mockMvc.perform(post("/api/v1/item/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // --------------------------------
    // GET /api/v1/item/
    // --------------------------------
    @Test
    void shouldReturnAllUserItems() throws Exception {

        OutputItemDTO item = new OutputItemDTO();
        item.setName("phone");

        Mockito.when(itemService.getAllItems())
                .thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/item/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("phone"));
    }
}
