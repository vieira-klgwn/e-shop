package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.DailyClose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyCloseRepository extends JpaRepository<DailyClose, Long> {
}
