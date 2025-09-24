package vector.StockManagement.repositories;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.enums.Location;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdAndLocation(Long id, Location location);
}
