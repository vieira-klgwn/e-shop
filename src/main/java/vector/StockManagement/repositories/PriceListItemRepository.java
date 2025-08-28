package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PriceListItemRepository extends JpaRepository<PriceList, Long> {
}
