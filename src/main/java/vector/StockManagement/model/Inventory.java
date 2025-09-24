package vector.StockManagement.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Filter;
import vector.StockManagement.model.enums.LocationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@org.hibernate.annotations.Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private LocationType locationType;

    @NotNull
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Product product;


    @NotNull
    @Min(0)
    @Column(name = "qty_on_hand", nullable = false)
    private Integer qtyOnHand = 0;

    @NotNull
    @Min(0)
    @Column(name = "qty_reserved")
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Tenant tenant;


    public Inventory(LocationType locationType, Long locationId, Product product, Tenant tenant) {
        this.locationType = locationType;
        this.locationId = locationId;
        this.product = product;
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

    public void addStock(Integer quantity) {
        if (quantity > 0) {
//            // Update average unit cost using weighted average
//            BigDecimal totalCost = avgUnitCost.multiply(BigDecimal.valueOf(qtyOnHand))
//                    .add(unitCost.multiply(BigDecimal.valueOf(quantity)));
//            int totalQty = qtyOnHand + quantity;
//
//            this.avgUnitCost = totalQty > 0 ? totalCost.divide(BigDecimal.valueOf(totalQty), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
//            this.qtyOnHand = totalQty;
            this.lastStockIn = LocalDateTime.now();
            qtyOnHand += quantity;
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
                ", locationType=" + locationType +
                ", locationId=" + locationId +
                ", product=" + (product != null ? product.getSku() : null) +
                ", qtyOnHand=" + qtyOnHand +
                ", qtyReserved=" + qtyReserved +
                '}';
    }
}