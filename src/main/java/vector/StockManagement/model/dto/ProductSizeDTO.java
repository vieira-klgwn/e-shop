package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.Product;

@Data
public class ProductSizeDTO {
    private Long productId;
    private String productSize;
    private Integer qtyOnHand;
    private Long quantityOrdered;
    private String productName;
    private Long price;
}
