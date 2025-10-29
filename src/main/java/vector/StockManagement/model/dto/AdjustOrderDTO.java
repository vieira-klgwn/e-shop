package vector.StockManagement.model.dto;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AdjustOrderDTO {
    private Long customerDiscount;
    private Map<Long, Long> partialQtys = new HashMap<>();
    private Long priceAdjustment;

    private Map<Long, Long> productPriceAdjustments = new HashMap<>();

}
