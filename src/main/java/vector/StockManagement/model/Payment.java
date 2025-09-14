package vector.StockManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import vector.StockManagement.model.enums.AdjustmentReason;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import vector.StockManagement.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Payment Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_invoice", columnList = "invoice_id"),
        @Index(name = "idx_payment_method", columnList = "method"),
        @Index(name = "idx_payment_paid_at", columnList = "paid_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod method;

    @Size(max = 100)
    @Column(name = "txn_ref")
    private String txnRef;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 3)
    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;


    public Payment(Invoice invoice, PaymentMethod method, BigDecimal amount, String currency, User postedBy) {
        this();
        this.invoice = invoice;
        this.method = method;
        this.amount = amount;
        this.currency = currency;
        this.postedBy = postedBy;
        this.tenant = invoice.getTenant();
        this.paymentStatus = PaymentStatus.PENDING;
    }



    @Override
    public String toString() {
        return "Payment{" +

                ", method=" + method +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paidAt=" + paidAt +
                '}';
    }
}
