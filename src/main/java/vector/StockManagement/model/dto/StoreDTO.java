package vector.StockManagement.model.dto;

import lombok.Data;

@Data
public class StoreDTO {
    private String name;
    private String code;
    private String address;
    private String region;
    private String managerFirstName;
    private String managerLastName;
    private String managerEmail;
    private String managerPassword;
    private String managerConfirmPassword;
    private String managerGender;

}
