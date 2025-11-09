package vector.StockManagement.model.dto;

import lombok.Data;
import vector.StockManagement.model.OrderLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class OrderDTO {
    private LocalDateTime orderDate;
    @NotBlank
    private String orderNumber;
    @NotBlank
    private String orderLevel;
    @NotBlank
    private String deliveryAddress;

    @Positive
    private BigDecimal unitPrice;
    @NotEmpty
    private List<OrderLineDTO> orderLines;



    @Data
    public static class OrderLineDTO {
        @NotNull @Positive
        private Long productId;
        @NotNull @Positive
        private Integer qty;
        @NotNull @Positive
        private Integer qtyReserved;



        @NotNull
        private Map<String, Integer> sizes;


    }
}