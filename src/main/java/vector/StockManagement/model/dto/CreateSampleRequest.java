package vector.StockManagement.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateSampleRequest {

    private List<SampleItemDto> items;
    private String notes;
    private Long distributorId;

}