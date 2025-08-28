package vector.StockManagement.services;

import vector.StockManagement.model.StockTransaction;

import java.util.List;

public interface StockTransactionService {
    List<StockTransaction> findAll();
    StockTransaction findById(Long id);
    StockTransaction save(StockTransaction stockTransaction);
    void delete(Long id);
}