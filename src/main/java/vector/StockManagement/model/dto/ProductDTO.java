package vector.StockManagement.model.dto;

import lombok.Data;
import org.w3c.dom.stylesheets.LinkStyle;
import vector.StockManagement.model.ProductSize;

import java.util.List;

@Data
public class ProductDTO {
    private String productName;
    private List<ProductSize> productSizes;

    @Data
    public static class ProductSize{
        private String name;
        private Long price;
    }
}
