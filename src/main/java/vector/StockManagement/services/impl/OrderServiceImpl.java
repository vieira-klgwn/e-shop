package vector.StockManagement.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
//import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.OrderDTO;
import vector.StockManagement.model.dto.OrderDisplayDTO;
import vector.StockManagement.model.enums.*;
import vector.StockManagement.repositories.*;
import vector.StockManagement.services.OrderService;
import vector.StockManagement.services.InventoryService;
import vector.StockManagement.services.NotificationSerivice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final InvoiceRepository invoiceRepository;
    private final StoreRepository storeRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;
    private final NotificationSerivice notificationService;
    private final InventoryRepository inventoryRepository;

    @Override
    public List<OrderDisplayDTO> findAll() {

        List<OrderDisplayDTO> displayDTOs = orderRepository.findAll().stream().map(this::getOrderDisplayDTO).collect(Collectors.toList());

        return displayDTOs;
    }

    private OrderDisplayDTO getOrderDisplayDTO(Order order) {
        OrderDisplayDTO orderDisplayDTO = new OrderDisplayDTO();
        orderDisplayDTO.setOrderNumber(order.getNumber());
        orderDisplayDTO.setUpdatedAt(order.getUpdatedAt());
        orderDisplayDTO.setCreatedAt(order.getCreatedAt());
        orderDisplayDTO.setOrderCurrency(order.getCurrency());
        orderDisplayDTO.setOrderLevel(order.getLevel().toString());
        orderDisplayDTO.setOrderStatus(order.getStatus().toString());
        orderDisplayDTO.setDeliveryDate(order.getDeliveryDate());
        orderDisplayDTO.setDeliveryAddress(order.getDeliveryAddress());
        orderDisplayDTO.setDeliveryDate(order.getDeliveryDate());
        orderDisplayDTO.setOrderAmount(order.getOrderAmount());
        orderDisplayDTO.setCreatedBy(order.getCreatedBy().getEmail());
        return orderDisplayDTO;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DISTRIBUTOR', 'STORE_MANAGER', 'SALES_MANAGER', 'ACCOUNTANT')")
    public OrderDisplayDTO findByIdDisplayed(Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));

//        Hibernate.initialize(order.getCreatedBy());
//        Hibernate.initialize(order.getDeliveryAddress());

        return getOrderDisplayDTO(order);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Transactional
    @Override
    @PreAuthorize("hasAnyRole('DISTRIBUTOR', 'STORE_MANAGER', 'ADMIN')")
    public Order save(Long userId, OrderDTO orderDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Tenant tenant = user.getTenant();
        
        if (tenant == null) {
            throw new RuntimeException("User must belong to a tenant");
        }


        Order order = new Order();
        order.setTenant(tenant);
        order.setCreatedBy(user);
        order.setLevel(OrderLevel.L1);
        order.setNumber("ORD-" + System.currentTimeMillis());
        order.setDeliveryAddress(orderDto.getDeliveryAddress());
        order.setDeliveryDate(LocalDateTime.now().plusDays(1));
        order.setStatus(OrderStatus.DRAFT);
        order.setCurrency("USD");


///  here you can only register an order if you have an orderline and an order line should also have a product,
        Order savedOrder = orderRepository.saveAndFlush(order);
        
        // Create order lines and calculate totals
        Long totalAmount = 0L;
        if (orderDto.getOrderLines() != null && !orderDto.getOrderLines().isEmpty()) {
            for (OrderDTO.OrderLineDTO lineDto: orderDto.getOrderLines()) {
                Product product = productRepository.findById(lineDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

                OrderLine orderLine = new OrderLine();
                orderLine.setOrder(savedOrder);
                orderLine.setProduct(product);
                orderLine.setQty(lineDto.getQty());
                orderLine.setUnitPrice(product.getPrice());
                orderLine.setLineTotal(product.getPrice() * lineDto.getQty());
                orderLine.setTenant(tenant);
                orderLineRepository.save(orderLine);
                totalAmount += orderLine.getLineTotal();
                savedOrder.getOrderLines().add(orderLine);
                Inventory inventory = inventoryRepository.findByProduct(product);
                inventory.reserveStock(lineDto.getQty());
            }

        }

        
        savedOrder.setOrderAmount(totalAmount);
        savedOrder.setStatus(OrderStatus.DRAFT);
        orderRepository.save(savedOrder);
        return savedOrder;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        Order order = findById(id);
        if (order.getStatus() != OrderStatus.DRAFT && order.getStatus() != OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot delete order that is not in DRAFT or CANCELLED status");
        }
        orderRepository.deleteById(id);
    }
    
    @Transactional
    public Order fulfillOrder(Long orderId, Long userId) {
        Order order = findById(orderId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (order.getStatus() != OrderStatus.APPROVED) {
            throw new RuntimeException("Can only fulfill approved orders");
        }
        
        // Process inventory movement
        for (OrderLine orderLine : order.getOrderLines()) {
            Inventory inventory = inventoryRepository.findByProduct(orderLine.getProduct());

            if (inventory != null) {
                // Release reserved stock and remove from inventory
                inventory.releaseReservedStock(orderLine.getQty());
                inventory.removeStock(orderLine.getQty());
                inventoryRepository.save(inventory);
                
                // Create stock transaction record
                // This would be implemented in StockTransactionService
            }
        }
        
        order.setStatus(OrderStatus.FULFILLED);
        createOrderNotifications(order, "Order Fulfilled");
        
        return orderRepository.save(order);
    }
    
    public Order submitOrder(Long orderId, Long userId) {
        Order order = findById(orderId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!order.canBeSubmitted()) {
            throw new RuntimeException("Order cannot be submitted");
        }
        
        order.submit();
        createOrderNotifications(order, "Order Submitted");
        
        return orderRepository.save(order);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STORE_MANAGER', 'DISTRIBUTOR')")
    public Order update(Long id, OrderDTO orderDto) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Only allow updates if order is still in DRAFT status
        if (existing.getStatus() != OrderStatus.DRAFT) {
            throw new RuntimeException("Cannot update order that is not in DRAFT status");
        }
        
        if (orderDto.getDeliveryAddress() != null) {
            existing.setDeliveryAddress(orderDto.getDeliveryAddress());
        }
        if (orderDto.getOrderDate() != null) {
            existing.setDeliveryDate(orderDto.getOrderDate());
        }
        
        return orderRepository.save(existing);
    }

    @Transactional
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_MANAGER','ACCOUNTANT')")
    public Order approve(Long userId, Order order) {
        User approver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        if (!order.canBeApproved()) {
            throw new RuntimeException("Order cannot be approved in current status: " + order.getStatus());
        }
        
        // Approve the order
        order.approve(approver);
        
        // Reserve inventory for order lines
        for (OrderLine orderLine : order.getOrderLines()) {
            Inventory inventory = inventoryRepository.findByProductAndLocationTypeAndLocationId(
                    orderLine.getProduct(), LocationType.WAREHOUSE, order.getWarehouse().getId());
            
            if (inventory == null) {
                throw new RuntimeException("No inventory found for product: " + orderLine.getProduct().getSku());
            }
            
            if (!inventory.canReserve(orderLine.getQty())) {
                throw new RuntimeException("Insufficient inventory for product: " + orderLine.getProduct().getSku() + 
                        ". Available: " + inventory.getQtyAvailable() + ", Required: " + orderLine.getQty());
            }
            
            inventory.reserveStock(orderLine.getQty()); // you can also do this using the transferservice instead
            inventoryRepository.save(inventory);
        }
        
        // Generate invoice
        Invoice invoice = createInvoiceFromOrder(order, approver);
        invoiceRepository.save(invoice);
        
        // Send notifications
        createOrderNotifications(order, "Order Approved");
        
        return orderRepository.save(order);
    }
    
    private Invoice createInvoiceFromOrder(Order order, User issuedBy) {
        Invoice invoice = new Invoice();
        invoice.setNumber("INV-" + System.currentTimeMillis());
        invoice.setOrder(order);
        invoice.setStore(order.getStore());
        invoice.setTenant(order.getTenant());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setIssuedBy(issuedBy);
        invoice.setCurrency(order.getCurrency());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        
        // Set amounts
//        Map<String, BigDecimal> amounts = new HashMap<>();
//        BigDecimal totalAmount = BigDecimal.valueOf(order.getOrderAmount());
//        amounts.put("net", totalAmount);
//        amounts.put("tax", BigDecimal.ZERO); // Can be calculated based on tax rules
//        amounts.put("total", totalAmount);
//        amounts.put("paid", BigDecimal.ZERO);
//        amounts.put("balance", totalAmount);
//        invoice.setAmounts(amounts);
        
        return invoice;
    }
    
    private void createOrderNotifications(Order order, String eventType) {
        try {
            // Notification to store
            if (order.getStore() != null) {
                Notification storeNotification = getNotification(eventType, ": Order ", order, "Order " + order.getNumber() + " has been " + eventType.toLowerCase() +
                        ". Delivery scheduled for " + order.getDeliveryDate());
                notificationService.save(storeNotification);
            }
            
            // Notification to warehouse
            if (order.getWarehouse() != null) {
                Notification warehouseNotification = getNotification(eventType, ": Prepare Order ", order, "Please prepare order " + order.getNumber() +
                        " for delivery to " + order.getDeliveryAddress());
                notificationService.save(warehouseNotification);
            }
            //Notification to distributor
            if (order.getCreatedBy() != null) {
                Notification distributorNotification = getNotification(eventType, " Distributor ", order, "Please distribute order " + order.getNumber());
                order.getCreatedBy().getNotifications().add(distributorNotification);
                notificationService.save(distributorNotification);
            }

        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to create notifications: " + e.getMessage());
        }
    }

    private static Notification getNotification(String eventType, String x, Order order, String order1) {
        Notification storeNotification = new Notification();
        storeNotification.setType(NotificationType.ORDER_UPDATE);
        storeNotification.setChannel(NotificationChannel.EMAIL);
        storeNotification.setTitle(eventType);
        storeNotification.setSubject(eventType + x + order.getNumber());
        storeNotification.setMessage(order1);
        storeNotification.setTenant(order.getTenant());
        storeNotification.setReferenceType("ORDER");
        storeNotification.setReferenceId(order.getId());
        storeNotification.setType(NotificationType.ORDER_UPDATE);
        return storeNotification;
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_MANAGER')")
    public Order reject(Order order) {
        if (order.getStatus() != OrderStatus.SUBMITTED) {
            throw new RuntimeException("Can only reject submitted orders");
        }
        
        order.setStatus(OrderStatus.REJECTED);
        
        // Create notification
        createOrderNotifications(order, "Order Rejected");
        
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