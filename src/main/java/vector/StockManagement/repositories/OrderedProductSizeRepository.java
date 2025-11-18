package vector.StockManagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vector.StockManagement.model.OrderedProductSize;
import vector.StockManagement.model.User;

import java.util.List;

@Repository
public interface OrderedProductSizeRepository extends JpaRepository<OrderedProductSize, Long> {
    List<OrderedProductSize> findAllByCustomer(User customer);
}
