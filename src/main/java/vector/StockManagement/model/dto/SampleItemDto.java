package vector.StockManagement.model.dto;

// DTO: SampleItemDto.java

import lombok.Data;
import vector.StockManagement.model.ProductSize;

import java.util.List;

@Data
public class SampleItemDto {
    private Long productId;
    private List<ProductSize> items;

}