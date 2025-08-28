package vector.StockManagement.services;


import vector.StockManagement.model.Inventory;

import java.util.List;

public interface InventoryService {
    List<Inventory> findAll();
    Inventory findById(Long id);
    Inventory save(Inventory inventory);
    void delete(Long id);
}