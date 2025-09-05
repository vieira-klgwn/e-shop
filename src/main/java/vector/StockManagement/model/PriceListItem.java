package vector.StockManagement.model;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// Price List Item Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "price_list_items", indexes = {
        @Index(name = "idx_price_list_product", columnList = "price_list_id, product_id", unique = true)
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceListItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priceList")// removed nullable false
    private PriceList priceList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // removed , nullable = false
    private Product product;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private Long basePrice;

    @DecimalMin("0.0")
    @Column(name = "min_price", precision = 15, scale = 2)
    private Long minPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "taxes", columnDefinition = "json")
    private Map<String, Object> taxes = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "discounts", columnDefinition = "json")
    private Map<String, Object> discounts = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;



    public PriceListItem(PriceList priceList, Product product, Long basePrice) {
        this.priceList = priceList;
        this.product = product;
        this.basePrice = basePrice;
    }


    @Override
    public String toString() {
        return "PriceListItem{" +
                ", priceList=" + (priceList != null ? priceList.getName() : null) +
                ", product=" + (product != null ? product.getSku() : null) +
                ", basePrice=" + basePrice +
                '}';
    }
}