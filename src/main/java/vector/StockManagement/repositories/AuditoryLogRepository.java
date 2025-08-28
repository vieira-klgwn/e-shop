package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.AuditLog;


public interface AuditoryLogRepository extends JpaRepository<AuditLog, Long> {
}
