package vector.StockManagement.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PriceDisplayDTO {
    private Long productId;
    private String productName;
    private String sku;
    private FactoryPriceDTO factoryPrice;
    private DistributorPriceDTO distributorPrice;

    @Data
    public static class FactoryPriceDTO {
        private Long priceListId;
        private String priceListName;
        private BigDecimal basePrice;
        private BigDecimal minPrice;
        private LocalDate validFrom;
        private LocalDate validTo;
        private boolean isActive;
    }

    @Data
    public static class DistributorPriceDTO {
        private Long priceListId;
        private String priceListName;
        private BigDecimal basePrice;
        private BigDecimal minPrice;
        private LocalDate validFrom;
        private LocalDate validTo;
        private boolean isActive;
    }
}
