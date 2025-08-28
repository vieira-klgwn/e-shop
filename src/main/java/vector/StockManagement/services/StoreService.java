package vector.StockManagement.services;

import vector.StockManagement.model.Store;

import java.util.List;

public interface StoreService {
    List<Store> findAll();
    Store findById(Long id);
    Store save(Store store);
    void delete(Long id);
}