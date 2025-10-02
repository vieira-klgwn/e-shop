package vector.StockManagement.services;

import vector.StockManagement.model.Order;
import vector.StockManagement.model.dto.OrderDTO;
import vector.StockManagement.model.dto.OrderDisplayDTO;

import java.util.List;

public interface OrderService {
    List<OrderDisplayDTO> findAll();

    List<OrderDisplayDTO> getOrderDisplayDTOforStoreForDistributor();

    OrderDisplayDTO findByIdDisplayed(Long id);

    List<OrderDisplayDTO> findAllByDistributor(Long distributorId);

    List<OrderDisplayDTO> getOrdersFromRetailer(Long id);

    List<OrderDisplayDTO> getOrderDisplayDTOforStore(Long id);
    Order findById(Long id);
    Order save(Long userId,OrderDTO orderDto);
    void delete(Long id);
    Order update(Long id, OrderDTO orderDto);
    Order approve(Long userId,Order order);
    Order reject(Order order);


//    void submitOrder(Long orderId, User submitter);
}
