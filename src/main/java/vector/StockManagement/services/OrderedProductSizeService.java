package vector.StockManagement.services;

import org.springframework.stereotype.Repository;
import vector.StockManagement.model.OrderedProductSize;

import java.util.List;


public interface OrderedProductSizeService {
    OrderedProductSize createOrderedProductSize(OrderedProductSize orderedProductSize);
    OrderedProductSize findOrderedProductSizeById(Long id);
    List<OrderedProductSize> findOrderedProductSizeByCustomer(Long id);

    List<OrderedProductSize> findAllOrderedProductSizes();
}
