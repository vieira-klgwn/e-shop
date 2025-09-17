package vector.StockManagement.services;


import vector.StockManagement.model.CreateProductRequest;
import vector.StockManagement.model.PriceList;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.dto.PriceDisplayDTO;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(Long id);
    Product save(Product product);
    void delete(Long id);
    Product update(Long id, Product product);
    PriceDisplayDTO getProductPrices(Long productId, Long tenantId);
}