package vector.StockManagement.services;

import vector.StockManagement.model.ProductSize;

public interface ProductSizeService {
    ProductSize updateQtyOnHand(Long productId, String productSize, Integer qtyOnHand);

    ProductSize updatePriceBySize(Long productId, String productSize, Long price);

    ProductSize findProductSizeById(Long productSizeId);
}
