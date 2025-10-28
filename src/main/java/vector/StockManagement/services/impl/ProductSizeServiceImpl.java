package vector.StockManagement.services.impl;

import org.springframework.stereotype.Service;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.ProductSize;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.ProductSizeRepository;
import vector.StockManagement.services.ProductSizeService;

@Service
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    private final ProductRepository productRepository;

    public ProductSizeServiceImpl(ProductSizeRepository productSizeRepository, ProductRepository productRepository) {
        this.productSizeRepository = productSizeRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ProductSize updateQtyOnHand(Long productId, String productSize, Integer qtyOnHand) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        ProductSize sizeOfProduct = productSizeRepository.findByProductAndSize(product, productSize);
        sizeOfProduct.setQuantityInStock(sizeOfProduct.getQuantityInStock()+qtyOnHand);
        return productSizeRepository.save(sizeOfProduct);


    }

    @Override
    public ProductSize findProductSizeById(Long productSizeId) {
        return productSizeRepository.findById(productSizeId).orElseThrow(()->new IllegalArgumentException("Product not found"));
    }
}
