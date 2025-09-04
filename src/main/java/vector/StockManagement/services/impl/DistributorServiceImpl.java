package vector.StockManagement.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Distributor;
import vector.StockManagement.model.Store;
import vector.StockManagement.model.Warehouse;
import vector.StockManagement.repositories.DistributorRepository;
import vector.StockManagement.repositories.StoreRepository;
import vector.StockManagement.repositories.WarehouseRepository;
import vector.StockManagement.services.DistributorService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistributorServiceImpl implements DistributorService {

    private final DistributorRepository distributorRepository;
    private final WarehouseRepository warehouseRepository;
    private final StoreRepository storeRepository;

    @Override
    public List<Distributor> findAll() {
        return distributorRepository.findAll();
    }

    @Override
    public Distributor findById(Long id) {
        return distributorRepository.findById(id).orElse(null);
    }

    @Override
    public Distributor createDistributorByWarehouse(Long warehouseId, Distributor distributor) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        distributor.setWarehouse(warehouse);
        return distributorRepository.save(distributor);



    }


    @Override
    public Distributor createDistributorByStore(long storeId, Distributor distributor) {

        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("Store not found"));
        distributor.setStore(store);
        return distributorRepository.save(distributor);
    }

    @Override
    public Distributor save(Distributor distributor) {
        return distributorRepository.save(distributor);
    }


    @Override
    public void delete(Long id) {
        distributorRepository.deleteById(id);
    }
}