package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.OrderedProductSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TransferDTO {

    private Long orderId;
    private String reason;
    private Map<Long, Long> partialQtys = new HashMap<>();
    private String from;
    private List<OrderedProductSize> items = new ArrayList<>();

}
