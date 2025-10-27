package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.ProductSize;

import java.util.HashMap;
import java.util.Map;

@Data
public class PriceOfEachSizeDTO {
    Map<Long, Long> productSizePrices = new HashMap<>();
}
