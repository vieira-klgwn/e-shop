package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction,Long> {
}
