package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.DailyClose;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DailyCloseRepository extends JpaRepository<DailyClose, Long> {
}
