package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Order;
import vector.StockManagement.model.OrderLine;
import vector.StockManagement.repositories.OrderLineRepository;
import vector.StockManagement.repositories.OrderRepository;
import vector.StockManagement.services.OrderLineService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OrderLineServiceImpl implements OrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<OrderLine> findAll() {
        return orderLineRepository.findAll();
    }

    @Override
    public OrderLine findById(Long id) {
        return orderLineRepository.findById(id).orElse(null);
    }

    @Override
    public OrderLine save(OrderLine orderLine) {
        return orderLineRepository.save(orderLine);
    }

    @Override
    public OrderLine createOrderlineByOrder(Long orderId, OrderLine orderLine){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        orderLine.setOrder(order);
        return orderLineRepository.save(orderLine);

    }

    @Override
    public void delete(Long id) {
        orderLineRepository.deleteById(id);
    }
}