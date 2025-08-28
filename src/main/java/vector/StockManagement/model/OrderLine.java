package goma.gorilla.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// Order Line Entity
@Entity
@Table(name = "order_lines", indexes = {
        @Index(name = "idx_order_line_order", columnList = "order_id"),
        @Index(name = "idx_order_line_product", columnList = "product_id")
})
public class OrderLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private goma.gorilla.backend.model.Product product;

    @NotNull
    @Column(name = "qty", nullable = false)
    private Integer qty;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @DecimalMin("0.0")
    @Column(name = "discount", precision = 15, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin("0.0")
    @Column(name = "tax", precision = 15, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @DecimalMin("0.0")
    @Column(name = "line_total", precision = 15, scale = 2)
    private BigDecimal lineTotal;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @Column(name = "fulfilled_qty")
    private Integer fulfilledQty = 0;

    // Constructors
    public OrderLine() {}

    public OrderLine(Order order, goma.gorilla.backend.model.Product product, Integer qty, BigDecimal unitPrice) {
        this.order = order;
        this.product = product;
        this.qty = qty;
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    // Getters and Setters
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
        calculateLineTotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
        calculateLineTotal();
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
        calculateLineTotal();
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getFulfilledQty() {
        return fulfilledQty;
    }

    public void setFulfilledQty(Integer fulfilledQty) {
        this.fulfilledQty = fulfilledQty;
    }

    // Helper methods
    public void calculateLineTotal() {
        if (qty != null && unitPrice != null) {
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            BigDecimal discountAmount = discount != null ? discount : BigDecimal.ZERO;
            BigDecimal taxAmount = tax != null ? tax : BigDecimal.ZERO;

            this.lineTotal = subtotal.subtract(discountAmount).add(taxAmount);
        }
    }

    public Integer getRemainingQty() {
        return qty - fulfilledQty;
    }

    public boolean isFullyFulfilled() {
        return fulfilledQty.equals(qty);
    }

    public boolean canFulfill(Integer quantity) {
        return quantity <= getRemainingQty();
    }

    public void fulfill(Integer quantity) {
        if (canFulfill(quantity)) {
            this.fulfilledQty += quantity;
        }
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        calculateLineTotal();
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "id=" + getId() +
                ", product=" + (product != null ? product.getSku() : null) +
                ", qty=" + qty +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + lineTotal +
                '}';
    }
}