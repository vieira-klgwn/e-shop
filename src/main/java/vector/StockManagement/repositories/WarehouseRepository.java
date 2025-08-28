package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
