package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
