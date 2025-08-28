package goma.gorilla.backend.model;

import goma.gorilla.backend.model.enums.AdjustmentReason;
import goma.gorilla.backend.model.enums.LocationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

// Adjustment Entity
@Entity
@Table(name = "adjustments", indexes = {
        @Index(name = "idx_adjustment_location", columnList = "level, location_id"),
        @Index(name = "idx_adjustment_product", columnList = "product_id")
})
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
    private goma.gorilla.backend.model.Product product;

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

    // Constructors
    public Adjustment() {}

    public Adjustment(LocationType level, Long locationId, goma.gorilla.backend.model.Product product,
                      Integer qtyDelta, AdjustmentReason reason, User createdBy, Tenant tenant) {
        this.level = level;
        this.locationId = locationId;
        this.product = product;
        this.qtyDelta = qtyDelta;
        this.reason = reason;
        this.createdBy = createdBy;
        this.tenant = tenant;
    }

    // Getters and Setters
    public LocationType getLevel() {
        return level;
    }

    public void setLevel(LocationType level) {
        this.level = level;
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

    public Integer getQtyDelta() {
        return qtyDelta;
    }

    public void setQtyDelta(Integer qtyDelta) {
        this.qtyDelta = qtyDelta;
    }

    public AdjustmentReason getReason() {
        return reason;
    }

    public void setReason(AdjustmentReason reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

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
                "id=" + getId() +
                ", level=" + level +
                ", locationId=" + locationId +
                ", product=" + (product != null ? product.getSku() : null) +
                ", qtyDelta=" + qtyDelta +
                ", reason=" + reason +
                '}';
    }
}