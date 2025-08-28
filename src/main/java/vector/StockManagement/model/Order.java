package vector.StockManagement.model;

import vector.StockManagement.model.enums.OrderChannel;
import vector.StockManagement.model.enums.OrderLevel;
import vector.StockManagement.model.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Order Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_number", columnList = "number", unique = true),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_distributor", columnList = "distributor_id"),
        @Index(name = "idx_order_store", columnList = "store_id"),
        @Index(name = "idx_order_created_at", columnList = "created_at")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private OrderLevel level;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private OrderChannel channel = OrderChannel.WEB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distributor_id")
    private Distributor distributor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.DRAFT;

    @NotBlank
    @Size(max = 3)
    @Column(name = "currency", nullable = false)
    private String currency;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "totals", columnDefinition = "json")
    private Map<String, BigDecimal> totals = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @Size(max = 500)
    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderLine> orderLines = new ArrayList<>();

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;



    public Order(OrderLevel level, String currency, User createdBy, Tenant tenant) {
        this();
        this.level = level;
        this.currency = currency;
        this.createdBy = createdBy;
        this.tenant = tenant;
        this.number = generateOrderNumber();
    }

    // Helper methods
    private void initializeTotals() {
        totals.put("subtotal", BigDecimal.ZERO);
        totals.put("tax", BigDecimal.ZERO);
        totals.put("discount", BigDecimal.ZERO);
        totals.put("grandTotal", BigDecimal.ZERO);
    }

    public BigDecimal getSubtotal() {
        return totals.getOrDefault("subtotal", BigDecimal.ZERO);
    }

    public BigDecimal getTax() {
        return totals.getOrDefault("tax", BigDecimal.ZERO);
    }

    public BigDecimal getDiscount() {
        return totals.getOrDefault("discount", BigDecimal.ZERO);
    }

    public BigDecimal getGrandTotal() {
        return totals.getOrDefault("grandTotal", BigDecimal.ZERO);
    }

    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.setOrder(this);
        recalculateTotals();
    }

    public void removeOrderLine(OrderLine orderLine) {
        orderLines.remove(orderLine);
        orderLine.setOrder(null);
        recalculateTotals();
    }

    public void recalculateTotals() {
        BigDecimal subtotal = orderLines.stream()
                .map(OrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = orderLines.stream()
                .map(OrderLine::getTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = orderLines.stream()
                .map(OrderLine::getDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandTotal = subtotal.add(tax).subtract(discount);

        totals.put("subtotal", subtotal);
        totals.put("tax", tax);
        totals.put("discount", discount);
        totals.put("grandTotal", grandTotal);
    }

    public boolean canBeSubmitted() {
        return status == OrderStatus.DRAFT && !orderLines.isEmpty();
    }

    public boolean canBeApproved() {
        return status == OrderStatus.SUBMITTED;
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.DRAFT || status == OrderStatus.SUBMITTED || status == OrderStatus.APPROVED;
    }

    public void submit() {
        if (canBeSubmitted()) {
            this.status = OrderStatus.SUBMITTED;
            this.submittedAt = LocalDateTime.now();
        }
    }

    public void approve(User approver) {
        if (canBeApproved()) {
            this.status = OrderStatus.APPROVED;
            this.approvedBy = approver;
            this.approvedAt = LocalDateTime.now();
        }
    }

    public void cancel(String reason) {
        if (canBeCancelled()) {
            this.status = OrderStatus.CANCELLED;
            this.cancelledAt = LocalDateTime.now();
            this.cancellationReason = reason;
        }
    }

    private String generateOrderNumber() {
        // This should be implemented with a proper sequence generator
        // For now, using a simple timestamp-based approach
        return "ORD-" + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Order{" +
                ", number='" + number + '\'' +
                ", level=" + level +
                ", status=" + status +
                ", grandTotal=" + getGrandTotal() +
                ", currency='" + currency + '\'' +
                '}';
    }
}
