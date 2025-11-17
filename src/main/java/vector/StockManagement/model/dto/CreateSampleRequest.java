package vector.StockManagement.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vector.StockManagement.model.ProductSize;

import java.util.List;
import java.util.Map;

@Data
public class CreateSampleRequest {


    private String notes;
    private Long customerId;

    private List<CreateSampleItemRequest> items;


}