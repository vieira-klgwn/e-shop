package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.ProductSize;

import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    ProductSize findByProductAndSize(Product product, String size);

    ProductSize findBySize(String size);

    List<ProductSize> size(String size);


    List<ProductSize> findByProduct(Product product);
}
