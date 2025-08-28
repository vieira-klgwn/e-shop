package vector.StockManagement.services;


import vector.StockManagement.model.Transfer;

import java.util.List;

public interface TransferService {
    List<Transfer> findAll();
    Transfer findById(Long id);
    Transfer save(Transfer transfer);
    void delete(Long id);
}