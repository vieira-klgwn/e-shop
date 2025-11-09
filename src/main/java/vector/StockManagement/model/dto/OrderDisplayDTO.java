package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.OrderLine;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.User;

import java.time.LocalDateTime;
import java.util.List;

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
    private User createdBy;
    private User approvedBy;
    private List<OrderLineDTO> orderLines;

    @Data
    public static class OrderLineDTO{
        private String productName;
//        private Integer quantity;
        private Long lineTotal;
        private Long price;
        private List<OrderProductSizeDTO> productSizes;
    }

}
