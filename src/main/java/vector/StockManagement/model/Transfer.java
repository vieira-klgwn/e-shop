package goma.gorilla.backend.model;

import goma.gorilla.backend.model.enums.AdjustmentReason;
import goma.gorilla.backend.model.enums.LocationType;
import goma.gorilla.backend.model.enums.TransferStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
// Transfer Entity
@Entity
@Table(name = "transfers", indexes = {
        @Index(name = "idx_transfer_from_location", columnList = "from_level, from_location_id"),
        @Index(name = "idx_transfer_to_location", columnList = "to_level, to_location_id"),
        @Index(name = "idx_transfer_status", columnList = "status")
})
public class Transfer extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "from_level", nullable = false)
    private LocationType fromLevel;

    @NotNull
    @Column(name = "from_location_id", nullable = false)
    private Long fromLocationId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "to_level", nullable = false)
    private LocationType toLevel;

    @NotNull
    @Column(name = "to_location_id", nullable = false)
    private Long toLocationId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransferStatus status = TransferStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // Constructors
    public Transfer() {}

    public Transfer(LocationType fromLevel, Long fromLocationId, LocationType toLevel,
                    Long toLocationId, User createdBy, Tenant tenant) {
        this.fromLevel = fromLevel;
        this.fromLocationId = fromLocationId;
        this.toLevel = toLevel;
        this.toLocationId = toLocationId;
        this.createdBy = createdBy;
        this.tenant = tenant;
    }

    // Getters and Setters
    public LocationType getFromLevel() {
        return fromLevel;
    }

    public void setFromLevel(LocationType fromLevel) {
        this.fromLevel = fromLevel;
    }

    public Long getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(Long fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public LocationType getToLevel() {
        return toLevel;
    }

    public void setToLevel(LocationType toLevel) {
        this.toLevel = toLevel;
    }

    public Long getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(Long toLocationId) {
        this.toLocationId = toLocationId;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
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

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    // Helper methods
    public void approve(User approver) {
        if (status == TransferStatus.DRAFT) {
            this.status = TransferStatus.APPROVED;
            this.approvedBy = approver;
            this.approvedAt = LocalDateTime.now();
        }
    }

    public void ship() {
        if (status == TransferStatus.APPROVED) {
            this.status = TransferStatus.IN_TRANSIT;
            this.shippedAt = LocalDateTime.now();
        }
    }

    public void receive() {
        if (status == TransferStatus.IN_TRANSIT) {
            this.status = TransferStatus.RECEIVED;
            this.receivedAt = LocalDateTime.now();
        }
    }

    public void cancel() {
        if (status == TransferStatus.DRAFT || status == TransferStatus.APPROVED) {
            this.status = TransferStatus.CANCELLED;
        }
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + getId() +
                ", fromLevel=" + fromLevel +
                ", fromLocationId=" + fromLocationId +
                ", toLevel=" + toLevel +
                ", toLocationId=" + toLocationId +
                ", status=" + status +
                '}';
    }
}
