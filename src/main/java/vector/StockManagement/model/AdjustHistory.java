package vector.StockManagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "adjust_history")
public class AdjustHistory {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long orderLineTotal;

    @ManyToOne
    @JoinColumn(name = "product_size_id")
    private ProductSize productSize;


}
