package vector.StockManagement.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.*;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.Role;
import vector.StockManagement.repositories.InventoryRepository;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.repositories.UserRepository;
import vector.StockManagement.services.InventoryService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;


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
    public boolean hasSufficientStock(Product product, Integer qty, LocationType locationType) {
        Inventory inventory = inventoryRepository.findByProductAndLocationType(product, locationType);
        if (inventory == null) return false;
        return inventory.getQtyOnHand() >= qty;
    }


    @Override
    public void releaseReservedStock(Product product, Integer qty, LocationType locationType) {
        Inventory inventory = inventoryRepository.findByProductAndLocationType(product, locationType);
        if (inventory == null) throw new RuntimeException("No inventory found for product at " + locationType);
        inventory.releaseReservedStock(qty);
        inventoryRepository.save(inventory);
    }

    @Override
    public void reserveStock(Product product, Integer qty, LocationType locationType) {
        Inventory inventory = inventoryRepository.findByProductAndLocationType(product, locationType);
        if (inventory == null) throw new RuntimeException("No inventory found for product at " + locationType);
        inventory.reserveStock(qty);
        inventoryRepository.save(inventory);
    }

    @Override
    public void removeStock(Product product, Integer qty, LocationType locationType) {
        Inventory inventory = inventoryRepository.findByProductAndLocationType(product, locationType);
        if (inventory == null) throw new RuntimeException("No inventory found for product at " + locationType);
        inventory.releaseReservedStock(qty);
        inventory.removeStock(qty);
        inventoryRepository.save(inventory);
    }

    @Override
    public void transferStock(Product product, Integer qty, LocationType from, LocationType to) {
        removeStock(product, qty, from);
        Inventory toInventory = inventoryRepository.findByProductAndLocationType(product, to);
        if (toInventory == null) {
            toInventory = new Inventory();
            toInventory.setProduct(product);
            toInventory.setLocationType(to);
            toInventory.setLocationId(1L);
            toInventory.setTenant(product.getTenant());
            toInventory.setQtyOnHand(0);
        }
        toInventory.addStock(qty);
        inventoryRepository.save(toInventory);
    }

    @Override
    public Inventory updateQtyOnHand(Long productId,Integer qtyOnHand) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));


        Inventory inventory = inventoryRepository.findByProductAndLocationType(product,LocationType.DISTRIBUTOR);
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory for product not found");
        }
        inventory.setQtyOnHand(inventory.getQtyOnHand() + qtyOnHand);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory findInventoryByProductAndUser(Long productId, Long userId){

        Product product = productRepository.findById(productId).orElseThrow(()->new IllegalArgumentException("Product not found"));

        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("User not found"));
        if (user.getRole()== Role.RETAILER){
            return inventoryRepository.findByProductAndLocationType(product, LocationType.DISTRIBUTOR);
        }
        else {
            return inventoryRepository.findByProductAndLocationType(product, LocationType.WAREHOUSE);
        }
    }

    @Override
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}
