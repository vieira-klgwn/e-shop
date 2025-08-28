package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTransactionRepository extends JpaRepository<StockTransaction,Long> {
}
