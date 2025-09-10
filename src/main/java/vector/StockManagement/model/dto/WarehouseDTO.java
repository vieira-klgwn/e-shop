package vector.StockManagement.model.dto;

import lombok.Data;

@Data
public class WarehouseDTO {
    private String name;
    private String address;
    private String region;
    private String code;
    private Long managerId;
    private Long tenantId;

}
