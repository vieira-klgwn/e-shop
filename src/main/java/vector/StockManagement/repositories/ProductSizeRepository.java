package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.ProductSize;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    ProductSize findByProductAndSize(Product product, String size);
}
