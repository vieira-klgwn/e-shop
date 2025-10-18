package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.Sample;

public interface SampleRepository extends JpaRepository<Sample, Long> {
}
