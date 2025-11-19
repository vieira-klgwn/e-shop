package vector.StockManagement.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.List;

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

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();



}
