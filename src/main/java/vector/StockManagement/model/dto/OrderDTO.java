package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.OrderLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private LocalDateTime orderDate;
    private String orderNumber;
    private String orderLevel;
    private String deliveryAddress;
    private Long productId;
    private Long userId;
    private Integer quantity;
    private BigDecimal unitPrice;
    List<OrderLine> orderLines;

}
