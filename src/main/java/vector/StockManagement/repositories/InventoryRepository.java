package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.Product;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByProduct(Product product);
}
