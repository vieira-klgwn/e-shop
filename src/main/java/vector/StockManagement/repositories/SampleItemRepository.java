package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.SampleItem;

@Repository
public interface SampleItemRepository extends JpaRepository<SampleItem, Long> {
}
