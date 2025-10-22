package vector.StockManagement.model.dto;

import lombok.Data;

@Data
public class TransferDTO {

    private Long orderId;
    private String reason;
    private Integer quantityToTransfer;
    private Integer quantityDelivered;
    private Long fromId;
    private Long toId;

}
