package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.Order;
import vector.StockManagement.model.OrderedProductSize;
import vector.StockManagement.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class InvoiceDisplayDTO {
    private Long invoiceId;
    private String invoiceNumber;
    private Long invoiceAmount;
    private LocalDate dueDate;
    private User issuedBy;
    private User orderBy;
    private OrderDisplayDTO order;
    private List<OrderedProductSize> orderedProductSizes = new ArrayList<>();
    private String currency;
    private String status;

}
