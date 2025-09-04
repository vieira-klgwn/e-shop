package vector.StockManagement.services;


import vector.StockManagement.model.OrderLine;

import java.util.List;

public interface OrderLineService {
    List<OrderLine> findAll();
    OrderLine findById(Long id);
    OrderLine save(OrderLine orderLine);

    OrderLine createOrderlineByOrder(Long orderId, OrderLine orderLine);

    void delete(Long id);
}