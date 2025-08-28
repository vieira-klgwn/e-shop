package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AdjustmentRepository extends JpaRepository<Adjustment, Long> {
}
