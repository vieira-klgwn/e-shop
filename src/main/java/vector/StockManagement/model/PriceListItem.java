package goma.gorilla.backend.model;

import goma.gorilla.backend.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// Price List Item Entity
@Entity
@Table(name = "price_list_items", indexes = {
        @Index(name = "idx_price_list_product", columnList = "price_list_id, product_id", unique = true)
})
public class PriceListItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id", nullable = false)
    private PriceList priceList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private goma.gorilla.backend.model.Product product;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    @DecimalMin("0.0")
    @Column(name = "min_price", precision = 15, scale = 2)
    private BigDecimal minPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "taxes", columnDefinition = "json")
    private Map<String, Object> taxes = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "discounts", columnDefinition = "json")
    private Map<String, Object> discounts = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public PriceListItem() {}

    public PriceListItem(PriceList priceList, goma.gorilla.backend.model.Product product, BigDecimal basePrice) {
        this.priceList = priceList;
        this.product = product;
        this.basePrice = basePrice;
    }

    // Getters and Setters
    public PriceList getPriceList() {
        return priceList;
    }

    public void setPriceList(PriceList priceList) {
        this.priceList = priceList;
    }

    public goma.gorilla.backend.model.Product getProduct() {
        return product;
    }

    public void setProduct(goma.gorilla.backend.model.Product product) {
        this.product = product;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public Map<String, Object> getTaxes() {
        return taxes;
    }

    public void setTaxes(Map<String, Object> taxes) {
        this.taxes = taxes;
    }

    public Map<String, Object> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Map<String, Object> discounts) {
        this.discounts = discounts;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "PriceListItem{" +
                "id=" + getId() +
                ", priceList=" + (priceList != null ? priceList.getName() : null) +
                ", product=" + (product != null ? product.getSku() : null) +
                ", basePrice=" + basePrice +
                '}';
    }
}