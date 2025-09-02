package vector.StockManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vector.StockManagement.model.enums.PriceListLevel;
import vector.StockManagement.model.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest{
    private String productName;
    private String sku;
    private String name;
    private Tenant tenant;
    private ProductCategory category;
    private BigDecimal basePrice;
    private BigDecimal minimumPrice;
    private BigDecimal maximumPrice;
    private Map<String, Object> taxes = new HashMap<>();
    private Map<String, Object> discounts = new HashMap<>();
    private Map<String, Object> attributes = new HashMap<>();
    private String barcode;
    private String imageUrl;
    private String code;
    private String productDescription;
    private int unit;
    private PriceList priceList;

}
