package vector.StockManagement.model.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;
import vector.StockManagement.model.enums.PaymentMethod;

import java.math.BigDecimal;

@Data
public class PaymentDTO {

    private Long invoiceId;

    private Long amount;

    private PaymentMethod method;
    private String txnRef;
}
