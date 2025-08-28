package vector.StockManagement.services;

import vector.StockManagement.model.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAll();
    Order findById(Long id);
    Order save(Order order);
    void delete(Long id);
}