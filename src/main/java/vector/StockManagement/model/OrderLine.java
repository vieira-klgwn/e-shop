package vector.StockManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Filter;

import javax.sound.sampled.Line;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// Order Line Entity
@EqualsAndHashCode(callSuper = true)
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "order_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;


    @Column(name = "qty")
    private Integer qty;

    @Column(name = "productSizes")
    @JsonIgnore
    @OneToMany(mappedBy = "orderLine", cascade = CascadeType.ALL, fetch = FetchType.EAGER)  // Or ManyToMany if join table
    private List<OrderedProductSize> productSizes = new ArrayList<>();



    @DecimalMin("0.0")
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private Long unitPrice;

    @DecimalMin("0.0")
    @Column(name = "discount", precision = 15, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin("0.0")
    @Column(name = "tax", precision = 15, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @DecimalMin("0.0")
    @Column(name = "line_total", precision = 15, scale = 2)
    private Long lineTotal;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @Column(name = "fulfilled_qty")
    private Integer fulfilledQty = 0;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;


    public OrderLine(Order order, Product product, Integer qty) {
        this.order = order;
        this.product = product;
        this.qty = qty;

//        calculateLineTotal();
    }


//    // Helper methods
//    public void calculateLineTotal() {
//        if (qty != null && unitPrice != null) {
//            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));
//            BigDecimal discountAmount = discount != null ? discount : BigDecimal.ZERO;
//            BigDecimal taxAmount = tax != null ? tax : BigDecimal.ZERO;
//
//            this.lineTotal = subtotal.subtract(discountAmount).add(taxAmount);
//        }
//    }

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

//    @PrePersist
//    @PreUpdate
//    public void prePersist() {
//        calculateLineTotal();
//    }

    @Override
    public String toString() {
        return "OrderLine{" +
                ", product=" + (product != null ? product.getSku() : null) +
                ", qty=" + qty +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + lineTotal +
                '}';
    }
}