package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.User;

import java.time.LocalDateTime;

@Data
public class OrderDisplayDTO {
    private Long orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String orderNumber;
    private String orderLevel;
    private String orderStatus;
    private String orderCurrency;
    private Long orderAmount;
    private String deliveryAddress;
    private LocalDateTime deliveryDate;
    private String createdBy;
}
