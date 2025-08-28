package vector.StockManagement.model;


import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.StockTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;


// Stock Transaction Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "stock_transactions", indexes = {
        @Index(name = "idx_stock_txn_product", columnList = "product_id"),
        @Index(name = "idx_stock_txn_type_level", columnList = "type, level"),
        @Index(name = "idx_stock_txn_timestamp", columnList = "timestamp"),
        @Index(name = "idx_stock_txn_ref", columnList = "ref_type, ref_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Product product;

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

    public StockTransaction(StockTransactionType type, LocationType level, Product product,
                            Integer qty, User createdBy, Tenant tenant) {
        this();
        this.type = type;
        this.level = level;
        this.product = product;
        this.qty = qty;
        this.createdBy = createdBy;
        this.tenant = tenant;
    }


    @Override
    public String toString() {
        return "StockTransaction{" +
                ", type=" + type +
                ", level=" + level +
                ", product=" + (product != null ? product.getSku() : null) +
                ", qty=" + qty +
                ", timestamp=" + timestamp +
                '}';
    }
}
