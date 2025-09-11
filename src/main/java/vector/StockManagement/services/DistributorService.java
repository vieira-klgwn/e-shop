package vector.StockManagement.services;

import vector.StockManagement.model.Distributor;
import vector.StockManagement.model.Store;
import vector.StockManagement.model.Warehouse;

import java.util.List;

public interface DistributorService {
    List<Distributor> findAll();
    Distributor findById(Long id);


    Distributor createDistributorByWarehouse(Long warehouseId, Distributor distributor);






    Distributor save(Distributor distributor);

    void delete(Long id);
}