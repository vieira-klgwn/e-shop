package vector.StockManagement.model.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

@Data
public class CreateSampleRequest {

    private List<SampleItemDto> items;
    private String notes;
    private Long distributorId;
    @Column(nullable = false)
    private Long productId;
    private Integer quantity;

}