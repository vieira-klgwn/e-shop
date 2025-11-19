package vector.StockManagement.model.dto;

import lombok.Data;
import org.w3c.dom.stylesheets.LinkStyle;
import vector.StockManagement.model.ProductSize;
import vector.StockManagement.model.enums.ProductStatus;

import java.util.List;

@Data
public class ProductDTO {
    private String productName;
    private List<ProductSize> productSizes;
    private String category;
    private String sku;
    private String description;
    private String unitOfMeasurement;
    private String  productStatus;

    @Data
    public static class ProductSize{
        private String name;
        private Long price;
        private Integer quantity;

    }
}
