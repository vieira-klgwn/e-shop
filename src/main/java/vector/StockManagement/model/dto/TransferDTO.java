package vector.StockManagement.model.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TransferDTO {

    private Long orderId;
    private String reason;
    private Integer quantityToTransfer;
    private Integer quantityDelivered;
    private Map<Long, Long> partialQtys = new HashMap<>();
    private Long fromId;
    private Long toId;

}
