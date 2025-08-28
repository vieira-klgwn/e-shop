package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
