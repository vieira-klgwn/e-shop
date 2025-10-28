package vector.StockManagement.services;

import vector.StockManagement.model.ProductSize;

public interface ProductSizeService {
    ProductSize updateQtyOnHand(Long productId, String productSize, Integer qtyOnHand);
    ProductSize findProductSizeById(Long productSizeId);
}
