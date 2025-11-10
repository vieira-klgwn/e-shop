package vector.StockManagement.services.impl;

import org.springframework.stereotype.Service;
import vector.StockManagement.model.AdjustHistory;
import vector.StockManagement.model.Order;
import vector.StockManagement.repositories.AdjustHistoryRepository;
import vector.StockManagement.repositories.OrderRepository;
import vector.StockManagement.services.AdjustHistoryService;

import java.util.List;

@Service
public class AdjustHistoryServiceImpl implements AdjustHistoryService {
    private final AdjustHistoryRepository adjustHistoryRepository;
    private final OrderRepository orderRepository;

    public AdjustHistoryServiceImpl(AdjustHistoryRepository adjustHistoryRepository, OrderRepository orderRepository) {
        this.adjustHistoryRepository = adjustHistoryRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public AdjustHistory save(AdjustHistory adjustHistory) {
        return null;
    }

    @Override
    public AdjustHistory findById(Long id) {
        return null;
    }

    @Override
    public AdjustHistory findByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return adjustHistoryRepository.findByOrder(order);
    }

    @Override
    public List<AdjustHistory> findAll() {
        return List.of();
    }

}
