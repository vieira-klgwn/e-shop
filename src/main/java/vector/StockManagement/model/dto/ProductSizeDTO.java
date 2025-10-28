package vector.StockManagement.model.dto;

import lombok.Data;

@Data
public class ProductSizeDTO {
    private Long productId;
    private String productSize;
    private Integer qtyOnHand;
}
