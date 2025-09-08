package vector.StockManagement.services;

import vector.StockManagement.auth.AuthenticationResponse;
import vector.StockManagement.model.CreateStoreRequest;
import vector.StockManagement.model.Store;
import vector.StockManagement.model.dto.StoreDTO;

import java.util.List;

public interface StoreService {
    List<Store> findAll();
    Store findById(Long id);
    AuthenticationResponse save(StoreDTO storeDTO);
    Store update(Long id,Store store);


    void delete(Long id);
}

