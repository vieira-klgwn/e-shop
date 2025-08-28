package goma.gorilla.backend.model;

import goma.gorilla.backend.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Entity
@Table(name = "invoices", indexes = {
        @Index(name = "idx_invoice_number", columnList = "number", unique = true),
        @Index(name = "idx_invoice_order", columnList = "order_id"),
        @Index(name = "idx_invoice_distributor", columnList = "distributor_id"),
        @Index(name = "idx_invoice_store", columnList = "store_id"),
        @Index(name = "idx_invoice_status", columnList = "status")
})
public class Invoice extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distributor_id")
    private Distributor distributor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private User issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @Column(name = "currency", length = 3)
    private String currency;

    // --- Constructors ---

    public Invoice() {
        initializeAmounts();
    }

    public Invoice(Order order, Tenant tenant) {
        this();
        this.order = order;
        this.tenant = tenant;
        this.distributor = order.getDistributor();
        this.store = order.getStore();
        this.currency = order.getCurrency();
        this.number = generateInvoiceNumber();
        copyAmountsFromOrder();
    }

    // --- Getters & Setters ---

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Distributor getDistributor() { return distributor; }
    public void setDistributor(Distributor distributor) { this.distributor = distributor; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public Map<String, BigDecimal> getAmounts() { return amounts; }
    public void setAmounts(Map<String, BigDecimal> amounts) { this.amounts = amounts; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public User getIssuedBy() { return issuedBy; }
    public void setIssuedBy(User issuedBy) { this.issuedBy = issuedBy; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    // --- Helper Methods ---

    private void initializeAmounts() {
        amounts.put("net", BigDecimal.ZERO);
        amounts.put("tax", BigDecimal.ZERO);
        amounts.put("total", BigDecimal.ZERO);
        amounts.put("paid", BigDecimal.ZERO);
        amounts.put("balance", BigDecimal.ZERO);
    }

    private void copyAmountsFromOrder() {
        amounts.put("net", order.getSubtotal());
        amounts.put("tax", order.getTax());
        amounts.put("total", order.getGrandTotal());
        amounts.put("paid", BigDecimal.ZERO);
        amounts.put("balance", order.getGrandTotal());
    }

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
                "id=" + getId() +
                ", number='" + number + '\'' +
                ", status=" + status +
                ", totalAmount=" + getTotalAmount() +
                ", balanceAmount=" + getBalanceAmount() +
                '}';
    }
}
