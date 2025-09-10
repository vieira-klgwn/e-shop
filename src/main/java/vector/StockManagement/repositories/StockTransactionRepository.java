package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction,Long> {
    List<StockTransaction> findByProduct(Product product);
    List<StockTransaction> findByTransactionDateBetween(LocalDateTime from, LocalDateTime to);
}
