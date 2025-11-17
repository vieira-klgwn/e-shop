package vector.StockManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_size")
@Data
public class ProductSize {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @ManyToOne
    @JoinColumn(name = "orderline")
    @JsonIgnore
    private OrderLine orderLine;

    @Column(name = "quantity_ordered")
    private Long quantityOrdered;

    @Column(name = "size")
    private String size;  // e.g., "250ml"

    @ManyToOne
    @JoinColumn(name = "sample_id")
    @JsonIgnore
    private SampleItem sampleItem;

    @Column(name = "price_of_this_size")
    private Long price;  // Per-size price (can override base)

    @Column(name = "quantity_in_stock")
    private Integer quantityInStock = 0;  // Track per size

}
