package vector.StockManagement.model.dto;

import lombok.Data;

@Data
public class TransferDTO {

    private Long oderId;
    private String reason;
    private Long quantityToTransfer;
    private Long quantityDelivered;
    private Long fromId;
    private Long toId;

}
