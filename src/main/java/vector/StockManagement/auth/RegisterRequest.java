package vector.StockManagement.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.model.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private Role role;
    private String gender;
    private Tenant tenant;
    private String phone;
    private String nationality;
}