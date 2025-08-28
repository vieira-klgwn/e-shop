package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
}
