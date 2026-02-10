package vector.StockManagement.model.dto;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import vector.StockManagement.model.Order;
import vector.StockManagement.model.User;
import vector.StockManagement.model.enums.AdjustmentStatus;
import vector.StockManagement.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Table(name = "adjust_order_dto")
@Entity
public class AdjustOrderDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerDiscount;

    @ElementCollection
    @CollectionTable(
            name = "adjust_order_partial_qtys",
            joinColumns = @JoinColumn(name = "adjust_order_dto_id")
    )
    @MapKeyColumn(name = "product_size_id")
    @Column(name = "partial_qty")
    private Map<Long, Long> partialQtys = new HashMap<>();

    private Long priceAdjustment;

    @ElementCollection
    @CollectionTable(
            name = "adjust_order_product_price_adjustments",
            joinColumns = @JoinColumn(name = "adjust_order_dto_id")
    )
    @MapKeyColumn(name = "product_size_id")
    @Column(name = "price_adjustment")
    private Map<Long, Long> productPriceAdjustments = new HashMap<>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AdjustmentStatus status = AdjustmentStatus.PENDING;


    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    private LocalDateTime approvedAt;


    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
}