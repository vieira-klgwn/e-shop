package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.Order;
import vector.StockManagement.model.dto.AdjustOrderDTO;

public interface AdjustOrderDTORepository extends JpaRepository<AdjustOrderDTO, Long> {

    AdjustOrderDTO findByOrder(Order order);
}
