package vector.StockManagement.model.dto;

import lombok.Data;

@Data
public class OrderProductSizeDTO {

            private String productSize;
            private Integer qtyOnHand;
            private Long quantityOrdered;
            private Long price;
}
