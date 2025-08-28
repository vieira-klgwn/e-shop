package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
