package vector.StockManagement.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.CreateProductRequest;
import vector.StockManagement.model.PriceList;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.model.Product;
import vector.StockManagement.repositories.PriceListItemRepository;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.services.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PriceListItemRepository priceListItemRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);// add in the functionality to also delete the priceList item
    }

    @Override
    public Product update(Long id, Product product) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {

           //add updates here
            return productRepository.save(existing);
        }
        return null;

    }
}