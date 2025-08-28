package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.PriceListItem;

@Repository
public interface PriceListItemRepository extends JpaRepository<PriceListItem, Long> {
}
