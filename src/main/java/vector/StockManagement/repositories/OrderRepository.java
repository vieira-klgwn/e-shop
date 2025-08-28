package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
