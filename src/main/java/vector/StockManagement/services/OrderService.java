package vector.StockManagement.services;

import vector.StockManagement.model.Order;
import vector.StockManagement.model.User;

import java.util.List;

public interface OrderService {
    List<Order> findAll();
    Order findById(Long id);
    Order save(Order order);
    void delete(Long id);
    Order approve(Order order);
    Order reject(Order order);

//    void submitOrder(Long orderId, User submitter);
}
