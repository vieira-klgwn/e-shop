package vector.StockManagement.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.enums.LocationType;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByProduct(Product product);

    Inventory findByProductAndLocationTypeAndLocationId(Product product, LocationType locationType, Long locationId);

    Inventory findByProductAndLocationType(Product product, LocationType locationType);

    List<Inventory> findAllByLocationType(@NotNull LocationType locationType);
}
