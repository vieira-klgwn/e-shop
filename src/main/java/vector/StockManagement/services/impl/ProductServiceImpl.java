package vector.StockManagement.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.config.TenantTransactionSynchronization;
import vector.StockManagement.model.*;
import vector.StockManagement.repositories.PriceListItemRepository;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.services.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PriceListItemRepository priceListItemRepository;
    private final TenantRepository tenantRepository;

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

        Tenant tenant = null;
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof User user) {
//            tenant = user.getTenant();
//        }

        tenant = tenantRepository.findById(TenantContext.getTenantId()).orElseThrow(() -> new RuntimeException("Tenant not found"));
        product.setTenant(tenant);
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);// TODO: cascade price list items if needed
    }

    @Override
    public Product update(Long id, Product product) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {
            if (product.getName() != null) existing.setName(product.getName());
            if (product.getSku() != null) existing.setSku(product.getSku());
            if (product.getDescription() != null) existing.setDescription(product.getDescription());
            if (product.getPrice() != null) existing.setPrice(product.getPrice());
            if (product.getUnit() != 0) existing.setUnit(product.getUnit());
            if (product.getSize() != null) existing.setSize(product.getSize());
            if (product.getCode() != null) existing.setCode(product.getCode());
            if (product.getCategory() != null) existing.setCategory(product.getCategory());
            if (product.getAttributes() != null) existing.setAttributes(product.getAttributes());
            if (product.getIsActive() != null) existing.setIsActive(product.getIsActive());
            if (product.getBarcode() != null) existing.setBarcode(product.getBarcode());
            if (product.getWeight() != null) existing.setWeight(product.getWeight());
            if (product.getImageUrl() != null) existing.setImageUrl(product.getImageUrl());
            if (product.getWarehouse() != null) existing.setWarehouse(product.getWarehouse());
            return productRepository.save(existing);
        }
        return null;

    }
}
