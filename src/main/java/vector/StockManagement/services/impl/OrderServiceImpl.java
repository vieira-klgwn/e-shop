package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Order;
import vector.StockManagement.model.OrderLine;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.OrderDTO;
import vector.StockManagement.model.enums.OrderStatus;
import vector.StockManagement.model.enums.Role;
import vector.StockManagement.repositories.OrderLineRepository;
import vector.StockManagement.repositories.OrderRepository;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.UserRepository;
import vector.StockManagement.services.OrderService;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();

    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order save( Long userId,OrderDTO orderDto) {
        Product product = productRepository.findById(orderDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Order order = new Order();
        order.setCreatedBy(user);
        order.setNumber(orderDto.getOrderNumber());
        order.setDeliveryAddress(orderDto.getDeliveryAddress());
        order.setDeliveryDate(LocalDateTime.now());
        order.setStatus(OrderStatus.SUBMITTED);
        List<OrderLine> lines = orderDto.getOrderLines().stream().map(
                lineReq -> {
                    OrderLine orderLine = new OrderLine();
                    orderLine.setOrder(order);
                    orderLine.setQty(lineReq.getQty());
                    orderLine.setUnitPrice(product.getPrice());
                    orderLine.setProduct(product);
                    orderLine.setLineTotal(product.getPrice() * orderLine.getUnitPrice());
                    return orderLineRepository.saveAndFlush(orderLine);
                }
        ).toList();
        order.setOrderLines(lines);
        return orderRepository.saveAndFlush(order);
    }

    @Override
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order update(Long id, OrderDTO order){
        Order existing = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (existing != null) {
            existing.setDeliveryDate(order.getOrderDate());
            existing.setDeliveryAddress(order.getDeliveryAddress());
            existing.setStatus(OrderStatus.APPROVED);
            orderRepository.save(existing);
            return existing;
        }
        else {
            throw new RuntimeException("Order not found");
        }
    }

    @Override
    public Order approve(Order order) {
        order.setStatus(OrderStatus.APPROVED);
        return orderRepository.save(order);

    }

    @Override
    public Order reject(Order order) {
        order.setStatus(OrderStatus.REJECTED);
        return orderRepository.save(order);
    }

//    @Override
//    public void submitOrder(Long orderId, User submitter){
//        Order order = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found"));
//        if(!order.canBeSubmitted()){
//            throw new IllegalArgumentException("Order cannot be submitted");
//
//        }
//        if (!hasRole(submitter, Role.DISTRIBUTOR) && !hasRole(submitter, Role.SALES_MANAGER)){
//            throw new SecurityException(("User not authorized to submit orders"));
//        }
//    }
}