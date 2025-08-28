package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoryLogRepository extends JpaRepository<Adjustment, Long> {
}
