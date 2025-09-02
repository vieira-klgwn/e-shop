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
    public Product save(CreateProductRequest t) {
        Product product = new Product();
        PriceListItem priceListItem = new PriceListItem();
        product.setName(t.getName());
        product.setAttributes(t.getAttributes());
        product.setCategory(t.getCategory());
        product.setCode(t.getCode());
        product.setBarcode(t.getBarcode());
        product.setDescription(t.getProductDescription());
        product.setImageUrl(t.getImageUrl());
        product.setPrice(t.getBasePrice());
        product.setSku(t.getSku());
        product.setUnit(t.getUnit());
        priceListItem.setProduct(product);
        priceListItem.setDiscounts(t.getDiscounts());
        priceListItem.setTaxes(t.getTaxes());
        priceListItem.setBasePrice(t.getBasePrice());
        priceListItem.setMinPrice(t.getBasePrice());
        priceListItem.setPriceList(t.getPriceList());
        productRepository.save(product);
        priceListItemRepository.save(priceListItem);
        return product;

    }


    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);// add in the functionality to also delete the priceList item
    }

    @Override
    public Product update(Long id, CreateProductRequest createProductRequest) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(createProductRequest.getName());
            existing.setAttributes(createProductRequest.getAttributes());
            existing.setCategory(createProductRequest.getCategory());
            //more and more
            return productRepository.save(existing);
        }
        return null;

    }
}