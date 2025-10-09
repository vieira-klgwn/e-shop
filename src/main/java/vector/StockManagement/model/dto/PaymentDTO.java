package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.User;
import vector.StockManagement.model.enums.PaymentMethod;

@Data
public class PaymentDTO {

    private Long invoiceId;

    private Long amount;

    private PaymentMethod method;
    private String txnRef;

    private User createdBy;
    private User orderBy;

}
