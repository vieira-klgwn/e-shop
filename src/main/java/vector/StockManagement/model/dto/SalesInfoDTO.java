package vector.StockManagement.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalesInfoDTO {
    Long paymentAmount;
    Long invoiceAmount;
    LocalDateTime salesInfoDateTime;
}
