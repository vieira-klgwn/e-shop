package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, Long> {
}
