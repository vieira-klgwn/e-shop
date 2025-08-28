package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributorRepository extends JpaRepository<Distributor, Long> {
}
