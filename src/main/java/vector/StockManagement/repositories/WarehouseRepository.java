package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
