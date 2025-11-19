package vector.StockManagement.model.dto;

import lombok.Data;

@Data
public class OrderProductSizeDTO {

            private String productSize;
            private Long productSizeId;
            private Integer qtyOnHand;
            private Long quantityOrdered;
            private Long price;

}
