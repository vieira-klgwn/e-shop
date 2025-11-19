package vector.StockManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Filter;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.TransferStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Transfer Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "transfers")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor
public class Transfer extends BaseEntity {


    @Enumerated(EnumType.STRING)
    @Column(name = "from_level")
    private LocationType fromLevel;


    @Column(name = "from_location_id")
    private Long fromLocationId;


    @Enumerated(EnumType.STRING)
    @Column(name = "to_level")
    private LocationType toLevel;


    @Column(name = "to_location_id")
    private Long toLocationId;

    @Column(name = "order_id")
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransferStatus status = TransferStatus.DRAFT;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "qty")
    private Integer qty;

    @OneToMany(mappedBy = "transfer")
    @JsonIgnore
    List<OrderedProductSize> items = new ArrayList<>();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Transfer(LocationType fromLevel, Long fromLocationId, LocationType toLevel,
                    Long toLocationId, User createdBy, Tenant tenant) {
        this.fromLevel = fromLevel;
        this.fromLocationId = fromLocationId;
        this.toLevel = toLevel;
        this.toLocationId = toLocationId;
        this.createdBy = createdBy;
        this.tenant = tenant;
    }

    // Getter/setter methods for service compatibility
    public LocationType getFromLocationType() { return fromLevel; }
    public LocationType getToLocationType() { return toLevel; }
    public Long getFromLocationId() { return fromLocationId; }
    public Long getToLocationId() { return toLocationId; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

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
                ", fromLevel=" + fromLevel +
                ", fromLocationId=" + fromLocationId +
                ", toLevel=" + toLevel +
                ", toLocationId=" + toLocationId +
                ", status=" + status +
                '}';
    }
}
