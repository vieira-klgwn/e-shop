package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
}
