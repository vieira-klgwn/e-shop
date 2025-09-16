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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            Tenant currentTenant = user.getTenant();
            if (currentTenant == null) {
                throw new IllegalStateException("Authenticated user has no tenant assigned");
            }
            inventory.setTenant(currentTenant);
            // Ensure product belongs to same tenant if present
            Product product = inventory.getProduct();
            if (product != null && product.getTenant() == null) {
                product.setTenant(currentTenant);
                productRepository.save(product);
            }
            return inventoryRepository.save(inventory);
        } else {
            throw new IllegalStateException("Authenticated principal is not a domain User");
        }
    }

    @Override
    public Inventory updateQtyOnHand(Long inventoryId, Integer qtyOnHand) {
        Inventory inventory = inventoryRepository.findById(inventoryId).orElseThrow(() -> new IllegalArgumentException("Invalid inventoryId: " + inventoryId));
        inventory.setQtyOnHand(qtyOnHand);
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
