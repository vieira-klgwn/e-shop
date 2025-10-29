package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.ProductSize;
import vector.StockManagement.model.enums.ProductCategory;

import java.util.List;

@Data
public class ProductDisplayDTO {
    private Long id;
    private String name;
    private String description;
    private Long price;
    private String imageUrl;
    private String category;
    private String size;
    private String code;
    private Integer qty;
    private String tenantName;
    private ProductCategory productCategory;

    private List<ProductSize> sizes;


}
