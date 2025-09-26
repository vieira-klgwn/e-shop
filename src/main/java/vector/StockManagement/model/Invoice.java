package vector.StockManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Filter;
import vector.StockManagement.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Invoice Entity
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@org.hibernate.annotations.Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "invoices")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Invoice extends BaseEntity implements TenantScoped {

    @NotBlank
    @Size(max = 50)
    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "invoice_amount", nullable = false)
    private Long invoiceAmount = 0L;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "distributor_id")
//    private Distributor distributor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @JsonIgnore
    private Store store;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "amounts", columnDefinition = "json")
    private Map<String, BigDecimal> amounts = new HashMap<>();

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issued_by")
    private User issuedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    @JsonIgnore
    private Tenant tenant;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @Column(name = "currency", length = 3)
    private String currency;


//    public Invoice(Order order, Tenant tenant) {
//        this();
//        this.order = order;
//        this.tenant = tenant;
////        this.distributor = order.getDistributor();
////        this.store = order.getStore();
//        this.currency = order.getCurrency();
//        this.number = generateInvoiceNumber();
//        copyAmountsFromOrder();
//    }



    // --- Helper Methods ---

    private void initializeAmounts() {
        amounts.put("net", BigDecimal.ZERO);
        amounts.put("tax", BigDecimal.ZERO);
        amounts.put("total", BigDecimal.ZERO);
        amounts.put("paid", BigDecimal.ZERO);
        amounts.put("balance", BigDecimal.ZERO);
    }

//    private void copyAmountsFromOrder() {
//        amounts.put("net", order.getSubtotal());
//        amounts.put("tax", order.getTax());
//        amounts.put("total", order.getGrandTotal());
//        amounts.put("paid", BigDecimal.ZERO);
//        amounts.put("balance", order.getGrandTotal());
//    }

    public BigDecimal getNetAmount() { return amounts.getOrDefault("net", BigDecimal.ZERO); }
    public BigDecimal getTaxAmount() { return amounts.getOrDefault("tax", BigDecimal.ZERO); }
    public BigDecimal getTotalAmount() { return amounts.getOrDefault("total", BigDecimal.ZERO); }
    public BigDecimal getPaidAmount() { return amounts.getOrDefault("paid", BigDecimal.ZERO); }
    public BigDecimal getBalanceAmount() { return amounts.getOrDefault("balance", BigDecimal.ZERO); }

    public void addPayment(BigDecimal amount) {
        BigDecimal newPaid = getPaidAmount().add(amount);
        BigDecimal newBalance = getTotalAmount().subtract(newPaid);

        amounts.put("paid", newPaid);
        amounts.put("balance", newBalance);
        updateStatus();
    }

    private void updateStatus() {
        BigDecimal balance = getBalanceAmount();
        BigDecimal total = getTotalAmount();

        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            this.status = InvoiceStatus.PAID;
        } else if (balance.compareTo(total) < 0) {
            this.status = InvoiceStatus.PARTIAL;
        }
    }

    public void issue(User issuer) {
        this.status = InvoiceStatus.ISSUED;
        this.issuedBy = issuer;
        this.issuedAt = LocalDateTime.now();
    }

    public void voidInvoice() {
        this.status = InvoiceStatus.VOID;
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Invoice{" +
                ", number='" + number + '\'' +
                ", status=" + status +
                ", totalAmount=" + getTotalAmount() +
                ", balanceAmount=" + getBalanceAmount() +
                '}';
    }
}
