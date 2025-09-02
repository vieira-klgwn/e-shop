package vector.StockManagement.services;


import vector.StockManagement.model.CreateProductRequest;
import vector.StockManagement.model.PriceList;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(Long id);
    Product save(CreateProductRequest createProductRequest);
    void delete(Long id);
    Product update(Long id, CreateProductRequest createProductRequest);
}