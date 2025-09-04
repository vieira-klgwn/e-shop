package vector.StockManagement.services;

import vector.StockManagement.model.CreateWareHouseRequest;
import vector.StockManagement.model.Warehouse;

import java.util.List;

public interface WarehouseService {
    List<Warehouse> findAll();
    Warehouse findById(Long id);
    Warehouse save(Warehouse warehouse);
    void delete(Long id);
}