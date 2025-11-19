package vector.StockManagement.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordered_product_size")
@Data
@NoArgsConstructor
public class OrderedProductSize {
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


    @ManyToOne
    @JoinColumn(name = "transfer_id")
    @JsonIgnore
    private Transfer transfer;

    @Column(name = "size")
    private String size;  // e.g., "250ml"


    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private User customer;


    @Column(name = "price_of_this_size")
    private Long price;  // Per-size price (can override base)

    @Column(name = "quantity_in_stock")
    private Integer quantityInStock = 0;  // Track per size

    @ManyToOne
    @JoinColumn(name = "sample_item")
    @JsonIgnore
    private SampleItem sampleItem;

    @Column(name = "isFulfilled")
    private Boolean isFulfilled = false;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();



}
