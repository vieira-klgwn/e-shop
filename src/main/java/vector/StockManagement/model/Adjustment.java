package vector.StockManagement.model;

import lombok.*;
import vector.StockManagement.model.enums.AdjustmentReason;
import vector.StockManagement.model.enums.LocationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

// Adjustment Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "adjustments", indexes = {
        @Index(name = "idx_adjustment_location", columnList = "level, location_id"),
        @Index(name = "idx_adjustment_product", columnList = "product_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Adjustment extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private LocationType level;

    @NotNull
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Column(name = "qty_delta", nullable = false)
    private Integer qtyDelta; // Positive for increase, negative for decrease

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private AdjustmentReason reason;

    @Size(max = 500)
    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "new_qty")
    private Integer newQty;

    @Column(name = "old_qty")
    private Integer oldQty;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private java.math.BigDecimal unitCost;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    public Adjustment(LocationType level, Long locationId, Product product,
                      Integer qtyDelta, AdjustmentReason reason, User createdBy, Tenant tenant) {
        this.level = level;
        this.locationId = locationId;
        this.product = product;
        this.qtyDelta = qtyDelta;
        this.reason = reason;
        this.createdBy = createdBy;
        this.tenant = tenant;
    }

    // Getter/setter methods for service compatibility
    public LocationType getLocationType() { return level; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public void setOldQty(Integer oldQty) { this.oldQty = oldQty; }


    // Helper methods
    public boolean isApproved() {
        return approvedBy != null && approvedAt != null;
    }

    public void approve(User approver) {
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Adjustment{" +
                ", level=" + level +
                ", locationId=" + locationId +
                ", product=" + (product != null ? product.getSku() : null) +
                ", qtyDelta=" + qtyDelta +
                ", reason=" + reason +
                '}';
    }
}