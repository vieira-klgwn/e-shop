package vector.StockManagement.services;


import vector.StockManagement.model.Inventory;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.enums.LocationType;

import java.util.List;

public interface InventoryService {
    List<Inventory> findAll();
    Inventory findById(Long id);
    Inventory save(Inventory inventory);

    boolean hasSufficientStock(Product product, Integer qty, LocationType locationType);

    void releaseReservedStock(Product product, Integer qty, LocationType locationType);

    void reserveStock(Product product, Integer qty, LocationType locationType);

    void removeStock(Product product, Integer qty, LocationType locationType);

    void transferStock(Product product, Integer qty, LocationType from, LocationType to);

    Inventory updateQtyOnHand(Long id, Integer qtyOnHand);


    Inventory findInventoryByProductAndUser(Long productId, Long userId);

    void delete(Long id);
}