package vector.StockManagement.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class PriceListItemDTO {
    @NotBlank
    private Long priceListId;
    @NotBlank
    private Long productId;
    @NotBlank
    private Long tenantId;
    @NotBlank
    private Long basePrice;
    private Long minPrice;
}
