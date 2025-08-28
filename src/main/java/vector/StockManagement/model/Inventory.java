package goma.gorilla.backend.model;


import goma.gorilla.backend.model.enums.LocationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "inventory", indexes = {
        @Index(name = "idx_inventory_location_product", columnList = "location_type, location_id, product_id", unique = true),
        @Index(name = "idx_inventory_product", columnList = "product_id"),
        @Index(name = "idx_inventory_location", columnList = "location_type, location_id")
})
public class Inventory extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private LocationType locationType;

    @NotNull
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private goma.gorilla.backend.model.Product product;

    @NotNull
    @Min(0)
    @Column(name = "qty_on_hand", nullable = false)
    private Integer qtyOnHand = 0;

    @NotNull
    @Min(0)
    @Column(name = "qty_reserved", nullable = false)
    private Integer qtyReserved = 0;

    @Min(0)
    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Min(0)
    @Column(name = "max_level")
    private Integer maxLevel;

    @DecimalMin("0.0")
    @Column(name = "avg_unit_cost", precision = 15, scale = 2)
    private BigDecimal avgUnitCost = BigDecimal.ZERO;

    @Column(name = "last_stock_in")
    private LocalDateTime lastStockIn;

    @Column(name = "last_stock_out")
    private LocalDateTime lastStockOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // Constructors
    public Inventory() {}

    public Inventory(LocationType locationType, Long locationId, goma.gorilla.backend.model.Product product, Tenant tenant) {
        this.locationType = locationType;
        this.locationId = locationId;
        this.product = product;
        this.tenant = tenant;
    }

    // Getters and Setters
    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public goma.gorilla.backend.model.Product getProduct() {
        return product;
    }

    public void setProduct(goma.gorilla.backend.model.Product product) {
        this.product = product;
    }

    public Integer getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(Integer qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public Integer getQtyReserved() {
        return qtyReserved;
    }

    public void setQtyReserved(Integer qtyReserved) {
        this.qtyReserved = qtyReserved;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    public BigDecimal getAvgUnitCost() {
        return avgUnitCost;
    }

    public void setAvgUnitCost(BigDecimal avgUnitCost) {
        this.avgUnitCost = avgUnitCost;
    }

    public LocalDateTime getLastStockIn() {
        return lastStockIn;
    }

    public void setLastStockIn(LocalDateTime lastStockIn) {
        this.lastStockIn = lastStockIn;
    }

    public LocalDateTime getLastStockOut() {
        return lastStockOut;
    }

    public void setLastStockOut(LocalDateTime lastStockOut) {
        this.lastStockOut = lastStockOut;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    // Helper methods
    public Integer getQtyAvailable() {
        return qtyOnHand - qtyReserved;
    }

    public boolean isLowStock() {
        return reorderLevel != null && qtyOnHand <= reorderLevel;
    }

    public boolean canReserve(Integer quantity) {
        return getQtyAvailable() >= quantity;
    }

    public void addStock(Integer quantity, BigDecimal unitCost) {
        if (quantity > 0) {
            // Update average unit cost using weighted average
            BigDecimal totalCost = avgUnitCost.multiply(BigDecimal.valueOf(qtyOnHand))
                    .add(unitCost.multiply(BigDecimal.valueOf(quantity)));
            Integer totalQty = qtyOnHand + quantity;

            this.avgUnitCost = totalQty > 0 ? totalCost.divide(BigDecimal.valueOf(totalQty), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
            this.qtyOnHand = totalQty;
            this.lastStockIn = LocalDateTime.now();
        }
    }

    public void removeStock(Integer quantity) {
        if (quantity > 0 && qtyOnHand >= quantity) {
            this.qtyOnHand -= quantity;
            this.lastStockOut = LocalDateTime.now();
        }
    }

    public void reserveStock(Integer quantity) {
        if (canReserve(quantity)) {
            this.qtyReserved += quantity;
        }
    }

    public void releaseReservedStock(Integer quantity) {
        if (quantity <= qtyReserved) {
            this.qtyReserved -= quantity;
        }
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + getId() +
                ", locationType=" + locationType +
                ", locationId=" + locationId +
                ", product=" + (product != null ? product.getSku() : null) +
                ", qtyOnHand=" + qtyOnHand +
                ", qtyReserved=" + qtyReserved +
                '}';
    }
}