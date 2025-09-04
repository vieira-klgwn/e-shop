package vector.StockManagement.services.impl;


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
import vector.StockManagement.services.InventoryService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory findById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    @Override
    public Inventory save(Inventory inventory) {

        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            Tenant currentTenant = user.getTenant();
            inventory.setTenant(currentTenant);
            return inventoryRepository.save(inventory);
        }
        else {
            throw new IllegalStateException("Make sure this authenticated user is of type UserDetails");
        }
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