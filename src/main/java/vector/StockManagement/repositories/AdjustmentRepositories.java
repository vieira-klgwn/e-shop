package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdjustmentRepositories extends JpaRepository<Adjustment, Long> {
}
