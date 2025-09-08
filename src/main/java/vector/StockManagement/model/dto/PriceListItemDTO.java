package vector.StockManagement.model.dto;

import lombok.Data;


@Data
public class PriceListItemDTO {
    private Long priceListId;
    private Long productId;
    private Long basePrice;
    private Long minPrice;
}
