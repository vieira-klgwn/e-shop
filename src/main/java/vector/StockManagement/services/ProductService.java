package vector.StockManagement.services;


import vector.StockManagement.model.CreateProductRequest;
import vector.StockManagement.model.PriceList;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.dto.PriceDisplayDTO;
import vector.StockManagement.model.dto.PriceOfEachSizeDTO;
import vector.StockManagement.model.dto.ProductDTO;
import vector.StockManagement.model.dto.ProductDisplayDTO;
import vector.StockManagement.model.enums.PriceListLevel;

import java.util.List;

public interface ProductService {

    List<ProductDisplayDTO> findAll();

    Product findById(Long id);
    ProductDisplayDTO findById1(Long id, PriceListLevel level);

    Product setPricesForEachSize(Product product, PriceOfEachSizeDTO dto);

    Product save(ProductDTO productDTO);

    List<ProductDisplayDTO> getAllStoreProducts();

    void delete(Long id);
    Product update(Long id, Product product);
    PriceDisplayDTO getProductPrices(Long productId, Long tenantId);
}