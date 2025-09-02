package vector.StockManagement.model.enums;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vector.StockManagement.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTenantRequest {

    private String name;
    private String code;
    private String description;
    private Map<String, String> settings = new HashMap<>();
    private List<User> users;


}
