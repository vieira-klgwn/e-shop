package vector.StockManagement.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.OrderDTO;
import vector.StockManagement.model.enums.OrderStatus;
import vector.StockManagement.model.enums.Role;
import vector.StockManagement.repositories.*;
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
    private final TenantRepository tenantRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();

    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public Order save( Long userId,OrderDTO orderDto) {
        Product product = productRepository.findById(orderDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Tenant tenant = user.getTenant();



        Order order = new Order();
        order.setTenant(tenant);
        order.setCreatedBy(user);
        order.setNumber(orderDto.getOrderNumber());
        order.setDeliveryAddress(orderDto.getDeliveryAddress());
        order.setDeliveryDate(LocalDateTime.now());
        order.setStatus(OrderStatus.SUBMITTED);

        Order savedOrder = orderRepository.saveAndFlush(order);
        List<OrderLine> lines = orderDto.getOrderLines().stream().map(
                lineReq -> {
                    OrderLine orderLine = new OrderLine();
                    orderLine.setOrder(order);
                    orderLine.setQty(lineReq.getQty());
                    orderLine.setUnitPrice(product.getPrice());
                    orderLine.setProduct(product);
                    orderLine.setLineTotal(product.getPrice() * lineReq.getQty());
                    return orderLineRepository.saveAndFlush(orderLine);
                }
        ).toList();
        order.setOrderLines(lines);
        for (OrderLine orderLine : orderDto.getOrderLines()) {
            Long amount = order.getOrderAmount();
            order.setOrderAmount(amount += orderLine.getProduct().getPrice() * orderLine.getQty());

        }
        return orderRepository.save(order);
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