package goma.gorilla.backend.model;

import goma.gorilla.backend.model.enums.LocationType;
import goma.gorilla.backend.model.enums.StockTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


// Stock Transaction Entity
@Entity
@Table(name = "stock_transactions", indexes = {
        @Index(name = "idx_stock_txn_product", columnList = "product_id"),
        @Index(name = "idx_stock_txn_type_level", columnList = "type, level"),
        @Index(name = "idx_stock_txn_timestamp", columnList = "timestamp"),
        @Index(name = "idx_stock_txn_ref", columnList = "ref_type, ref_id")
})
public class StockTransaction extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private StockTransactionType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private LocationType level;

    @Column(name = "from_location_id")
    private Long fromLocationId;

    @Column(name = "to_location_id")
    private Long toLocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private goma.gorilla.backend.model.Product product;

    @NotNull
    @Column(name = "qty", nullable = false)
    private Integer qty;

    @DecimalMin("0.0")
    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Size(max = 50)
    @Column(name = "ref_type")
    private String refType; // e.g., "ORDER", "TRANSFER", "ADJUSTMENT"

    @Column(name = "ref_id")
    private Long refId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    // Constructors
    public StockTransaction() {
        this.timestamp = LocalDateTime.now();
    }

    public StockTransaction(StockTransactionType type, LocationType level, goma.gorilla.backend.model.Product product,
                            Integer qty, User createdBy, Tenant tenant) {
        this();
        this.type = type;
        this.level = level;
        this.product = product;
        this.qty = qty;
        this.createdBy = createdBy;
        this.tenant = tenant;
    }

    // Getters and Setters
    public StockTransactionType getType() {
        return type;
    }

    public void setType(StockTransactionType type) {
        this.type = type;
    }

    public LocationType getLevel() {
        return level;
    }

    public void setLevel(LocationType level) {
        this.level = level;
    }

    public Long getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(Long fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public Long getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(Long toLocationId) {
        this.toLocationId = toLocationId;
    }

    public goma.gorilla.backend.model.Product getProduct() {
        return product;
    }

    public void setProduct(goma.gorilla.backend.model.Product product) {
        this.product = product;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
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

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "StockTransaction{" +
                "id=" + getId() +
                ", type=" + type +
                ", level=" + level +
                ", product=" + (product != null ? product.getSku() : null) +
                ", qty=" + qty +
                ", timestamp=" + timestamp +
                '}';
    }
}
