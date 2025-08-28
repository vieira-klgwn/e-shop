package goma.gorilla.backend.repositories;

import goma.gorilla.backend.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceListItemRepository extends JpaRepository<PriceList, Long> {
}
