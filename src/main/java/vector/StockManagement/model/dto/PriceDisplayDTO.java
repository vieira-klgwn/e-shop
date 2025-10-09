package vector.StockManagement.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PriceDisplayDTO {
    private Long productId;
    private String productName;
    private String sku;
    private List<FactoryPriceDTO> factoryPrices;
    private List<DistributorPriceDTO> distributorPrices;

    @Data
    public static class FactoryPriceDTO {
        private Long priceListId;
        private String priceListName;
        private Long basePrice;
        private Long minPrice;
        private LocalDate validFrom;
        private LocalDate validTo;
        private boolean isActive;
    }

    @Data
    public static class DistributorPriceDTO {
        private Long priceListId;
        private String priceListName;
        private Long basePrice;
        private Long minPrice;
        private LocalDate validFrom;
        private LocalDate validTo;
        private boolean isActive;
    }
}
