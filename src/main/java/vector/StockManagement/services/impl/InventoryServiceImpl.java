package vector.StockManagement.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Inventory;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.model.User;
import vector.StockManagement.model.enums.ProductCategory;
import vector.StockManagement.repositories.InventoryRepository;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.services.InventoryService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory findById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public Inventory save(Inventory inventory) {

//        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof User user) {
//            Tenant currentTenant = user.getTenant();
//            inventory.setTenant(currentTenant);
//            return inventoryRepository.save(inventory);
//        }
//        else {
//            throw new IllegalStateException("Make sure this authenticated user is of type UserDetails");
//        }
        Tenant tenant = new Tenant();
        tenant.setName("test");
        tenant.setDescription("test");
        tenant.setCode("111");
        tenantRepository.saveAndFlush(tenant);
        inventory.setTenant(tenant);
        Product product = new Product();
        product.setUnit(5);
        product.setName("sample_product1");
        product.setSku("111");
        product.setCode("111");
        product.setPrice(Long.parseLong("500"));
        product.setImageUrl("imageUrl");
        product.setDescription("description");
        product.setCategory(ProductCategory.ACTIVEWEAR_PANTS);
        product.setSize("200g");
        productRepository.saveAndFlush(product);
        inventory.setProduct(product);

        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory findInventoryByProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return inventoryRepository.findByProduct(product);
    }

    @Override
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}