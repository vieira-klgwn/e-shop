package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.AdjustHistory;
import vector.StockManagement.model.Order;

import java.util.List;

@Repository
public interface AdjustHistoryRepository extends JpaRepository<AdjustHistory, Long> {

    AdjustHistory findByOrder(Order order);
}
