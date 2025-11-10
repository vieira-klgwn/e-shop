package vector.StockManagement.services;

import vector.StockManagement.model.AdjustHistory;

import java.util.List;

public interface AdjustHistoryService {
    AdjustHistory save(AdjustHistory adjustHistory);
    AdjustHistory findById(Long id);
    AdjustHistory findByOrderId(Long orderId);
    List<AdjustHistory> findAll();

}
