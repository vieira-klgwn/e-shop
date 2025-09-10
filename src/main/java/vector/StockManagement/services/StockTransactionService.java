package vector.StockManagement.services;

import vector.StockManagement.model.StockTransaction;
import vector.StockManagement.model.Product;

import java.time.LocalDateTime;
import java.util.List;

public interface StockTransactionService {
    List<StockTransaction> findAll();
    StockTransaction findById(Long id);
    StockTransaction save(StockTransaction stockTransaction);
    void delete(Long id);
    List<StockTransaction> findByProduct(Product product);
    List<StockTransaction> findByDateRange(LocalDateTime from, LocalDateTime to);
}
