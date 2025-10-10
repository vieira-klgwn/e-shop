package vector.StockManagement.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
//import vector.StockManagement.config.TenantContext;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.OrderDTO;
import vector.StockManagement.model.dto.OrderDisplayDTO;
import vector.StockManagement.model.enums.*;
import vector.StockManagement.repositories.*;
import vector.StockManagement.services.OrderService;
import vector.StockManagement.services.InventoryService;
import vector.StockManagement.services.NotificationSerivice;
import vector.StockManagement.services.ProductService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private final PriceListItemRepository priceListItemRepository;
    private final PriceListRepository priceListRepository;
    private final ProductServiceImpl productServiceImpl;
    private final ProductService productService;
    private final JavaMailSender mailSender;

    @Override
    public List<OrderDisplayDTO> findAll() {

        List<OrderDisplayDTO> displayDTOs = orderRepository.findAll().stream().map(this::getOrderDisplayDTO).toList();

        List<OrderDisplayDTO> displays = new ArrayList<>();
        for (OrderDisplayDTO displayDTO : displayDTOs) {
            if (orderRepository.findById(displayDTO.getOrderId()).get().getLevel() == OrderLevel.L1) {
                displays.add(displayDTO);
            }
        }
        return displays;
    }

    @Override
    public List<OrderDisplayDTO> findAllByDistributor(Long distributorId) {
        User user = userRepository.findById(distributorId).orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderDisplayDTO> displayDTOs = orderRepository.findAllByCreatedBy(user).stream().map(this::getOrderDisplayDTO).toList();

        List<OrderDisplayDTO> displays = new ArrayList<>();
        for (OrderDisplayDTO displayDTO : displayDTOs) {
            if (orderRepository.findById(displayDTO.getOrderId()).get().getLevel() == OrderLevel.L1) {
                displays.add(displayDTO);
            }
        }
        return displays;
    }

    @Override
    public List<OrderDisplayDTO> getOrdersFromRetailer(Long accountantId) {
        User accountant = userRepository.findById(accountantId).orElseThrow(() -> new RuntimeException("Accountant not found"));
        User distributor = accountant.getDistributor();
        List<OrderDisplayDTO> ordersToDisplay = new ArrayList<>();
        List<Order> orders = orderRepository.findAll();
        List<Order> ordersFromRetailer = new ArrayList<>();

        for (Order order : orders) {
            if(order.getLevel() == OrderLevel.L2) {
                ordersFromRetailer.add(order);
            }
        }
        for (Order order : ordersFromRetailer) {
            if (order.getCreatedBy().getDistributor()==distributor){
                ordersToDisplay.add(getOrderDisplayDTO(order));


            }
        }


        return ordersToDisplay;
    }

    @Override
    public List<OrderDisplayDTO> getOrderDisplayDTOforStore(Long distributorId) {
        User user = userRepository.findById(distributorId).orElseThrow(() -> new RuntimeException("Distributor not found"));
        List<OrderDisplayDTO> displayDTOs = orderRepository.findAllByCreatedBy(user).stream().map(this::getOrderDisplayDTO).toList();
        List<OrderDisplayDTO> displays = new ArrayList<>();
        for (OrderDisplayDTO displayDTO : displayDTOs) {
            if (orderRepository.findById(displayDTO.getOrderId()).get().getLevel() == OrderLevel.L2) {
                displays.add(displayDTO);
            }
        }
        return displays;

    }

    @Override
    public List<OrderDisplayDTO> getOrderDisplayDTOforStoreForDistributor() {

        List<OrderDisplayDTO> displayDTOs = orderRepository.findAll().stream().map(this::getOrderDisplayDTO).toList();
        List<OrderDisplayDTO> displays = new ArrayList<>();
        for (OrderDisplayDTO displayDTO : displayDTOs) {
            if (orderRepository.findById(displayDTO.getOrderId()).get().getLevel() == OrderLevel.L2) {
                displays.add(displayDTO);
            }
        }
        return displays;

    }



    public OrderDisplayDTO getOrderDisplayDTO(Order order) {

        OrderDisplayDTO orderDisplayDTO = new OrderDisplayDTO();
        orderDisplayDTO.setOrderId(order.getId());
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
        orderDisplayDTO.setCreatedBy(order.getCreatedBy());
        orderDisplayDTO.setApprovedBy(order.getApprovedBy());



        List<OrderDisplayDTO.OrderLineDTO> lineDTOS = new ArrayList<>();

        for(OrderLine line: order.getOrderLines()) {

            OrderDisplayDTO.OrderLineDTO lineDTO = getOrderLineDTO(order, line);
            lineDTOS.add(lineDTO);
        }
        orderDisplayDTO.setOrderLines(lineDTOS);
        return orderDisplayDTO;
    }

    private static OrderDisplayDTO.OrderLineDTO getOrderLineDTO(Order order, OrderLine line) {
        Long productPrice = null;
        if (order.getLevel() == OrderLevel.L1) {
//                productPrice = productServiceImpl.getProductPrice(line.getProduct(), PriceListLevel.FACTORY);
            productPrice = line.getProduct().getFactoryPrice();
        }
        else {
//                productPrice = productServiceImpl.getProductPrice(line.getProduct(), PriceListLevel.DISTRIBUTOR);
            productPrice = line.getProduct().getDistributorPrice();
        }


        if (productPrice == null) {
            // Log for debugging: logger.warn("No price for product {} in order {}", line.getProduct().getId(), order.getId());
            productPrice = 0L;  // Or throw new RuntimeException("Missing price for product: " + line.getProduct().getName());
        }

        OrderDisplayDTO.OrderLineDTO lineDTO = new OrderDisplayDTO.OrderLineDTO();
        lineDTO.setProductName(line.getProduct().getName());
        lineDTO.setPrice(productPrice);
        lineDTO.setLineTotal(line.getQty()* productPrice);
        lineDTO.setQuantity(line.getQty());
        return lineDTO;
    }

    @Override
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
        order.setNumber("ORD-" + System.currentTimeMillis());
        order.setDeliveryAddress(orderDto.getDeliveryAddress());
        order.setDeliveryDate(LocalDateTime.now().plusDays(1));
        order.setStatus(OrderStatus.DRAFT);
        order.setCurrency("FRW");

        OrderLevel level = user.getRole().equals(Role.RETAILER) ? OrderLevel.L2 : OrderLevel.L1;

        System.out.println(level.toString());
        order.setLevel(level);




///  here you can only register an order if you have an orderline and an order line should also have a product,
        Order savedOrder = orderRepository.saveAndFlush(order);


        LocationType sourceLocation = (level == OrderLevel.L1) ? LocationType.WAREHOUSE : LocationType.DISTRIBUTOR;
        PriceListLevel priceLevel = (level == OrderLevel.L1) ? PriceListLevel.FACTORY : PriceListLevel.DISTRIBUTOR;

        
        // Create order lines and calculate totals
        Long totalAmount = 0L;
        if (orderDto.getOrderLines() != null && !orderDto.getOrderLines().isEmpty()) {
            for (OrderDTO.OrderLineDTO lineDto: orderDto.getOrderLines()) {
                Product product = productRepository.findById(lineDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

//                Inventory inventory = inventoryRepository.findByProductAndLocationType(product, sourceLocation);
                if (!inventoryService.hasSufficientStock(product, lineDto.getQty(), sourceLocation)) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getSku() + " at " + sourceLocation + " and its location is " + sourceLocation ) ;
                }


                // Get price based on level

//                Long price = getProductPrice(product, priceLevel);
                Long price = null;
                if (order.getLevel() == OrderLevel.L1) {
                    price = product.getFactoryPrice();
                }
                else {
                    price= product.getDistributorPrice();
                }





                if (price == -1L) {
                    throw new RuntimeException("No price found for product: " + product.getSku() + " at level " + priceLevel);
                }

                OrderLine orderLine = new OrderLine();

                if (lineDto.getQty() <= 0){
                    throw new RuntimeException("Order quantity should be a positive number and not zero");
                }

                orderLine.setOrder(savedOrder);
                orderLine.setProduct(product);
                orderLine.setQty(lineDto.getQty());
                orderLine.setUnitPrice(price);
                orderLine.setLineTotal(price * lineDto.getQty());
                orderLine.setTenant(tenant);
                orderLineRepository.save(orderLine);
                totalAmount += orderLine.getLineTotal();
                savedOrder.getOrderLines().add(orderLine);
//                inventoryService.reserveStock(product,lineDto.getQty(),sourceLocation); this was causing errors
            }

        }

        
        savedOrder.setOrderAmount(totalAmount);
        savedOrder.setStatus(OrderStatus.DRAFT);
        orderRepository.save(savedOrder);
        return savedOrder;
    }

    private Long getProductPrice(Product product, PriceListLevel priceLevel) {
        Long tenantId = TenantContext.getTenantId();
        List<PriceList> priceLists = priceListRepository.findByLevelAndIsActive(priceLevel, true);
//        List<PriceList> priceLists1 = priceListRepository.findByLevelAndIsActiveAndTenantId(priceLevel,true,tenantId);
        for (PriceList priceList : priceLists) {
            for (PriceListItem item : priceList.getItems()) {
                if (item.getProduct().getId().equals(product.getId())) {
                    return item.getBasePrice();
                }
            }
        }
        return -1L; // No price found
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

        LocationType sourceLocation = (order.getLevel() == OrderLevel.L1) ? LocationType.WAREHOUSE : LocationType.DISTRIBUTOR;
        LocationType targetLocation = (order.getLevel() == OrderLevel.L1) ? LocationType.DISTRIBUTOR : LocationType.RETAILER;
        // Process inventory movement
        for (OrderLine orderLine : order.getOrderLines()) {
            inventoryService.transferStock(orderLine.getProduct(), orderLine.getQty(), sourceLocation, targetLocation);
            inventoryService.releaseReservedStock(orderLine.getProduct(), orderLine.getQty(), sourceLocation);
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
    public Order approve(Long userId, Order order) {
        User approver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        if (!order.canBeApproved()) {
            throw new RuntimeException("Order cannot be approved in current status: " + order.getStatus());
        }
        
        // Approve the order
        order.approve(approver);
        LocationType type = LocationType.WAREHOUSE;

        if (order.getLevel()==OrderLevel.L1) {
            type = LocationType.WAREHOUSE;
        }
        else {
            type = LocationType.DISTRIBUTOR;
        }
        
        // Reserve inventory for order lines
        for (OrderLine orderLine : order.getOrderLines()) {
            Inventory inventory = inventoryRepository.findByProductAndLocationType(orderLine.getProduct(), type);
            
            if (inventory == null) {
                throw new RuntimeException("No inventory found for product: " + orderLine.getProduct().getSku());
            }
            
            if (!inventory.canReserve(orderLine.getQty())) {
                throw new RuntimeException("Insufficient inventory for product: " + orderLine.getProduct().getSku() + 
                        ". Available: " + inventory.getQtyAvailable() + ", Required: " + orderLine.getQty());
            }



            inventoryService.reserveStock(orderLine.getProduct(),orderLine.getQty(),type);
            inventoryRepository.save(inventory);
        }
        
        // Generate invoice
        Invoice invoice = createInvoiceFromOrder(order, approver);
        invoiceRepository.save(invoice);
        
        // Send notifications
        createOrderNotifications(order, "Order Approved");
        
        return orderRepository.save(order);
    }
    
    public Invoice createInvoiceFromOrder(Order order, User issuedBy) {
        Invoice invoice = new Invoice();
        invoice.setNumber("INV-" + System.currentTimeMillis());
        invoice.setOrder(order);
        invoice.setTenant(order.getTenant());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setIssuedBy(issuedBy);
        invoice.setCurrency(order.getCurrency());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setInvoiceAmount(order.getOrderAmount());
        
        // Set amounts
//        Map<String, BigDecimal> amounts = new HashMap<>();
//        BigDecimal totalAmount = BigDecimal.valueOf(order.getOrderAmount());
//        amounts.put("net", totalAmount);
//        amounts.put("tax", BigDecimal.ZERO); // Can be calculated based on tax rules
//        amounts.put("total", totalAmount);
//        amounts.put("paid", BigDecimal.ZERO);
//        amounts.put("balance", totalAmount);
//        invoice.setAmounts(amounts);

        
        return invoice; // Get price based on level

    }
    
    public void createOrderNotifications(Order order, String eventType) {
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
                Notification distributorNotification = getNotification(eventType, " Distributor ", order, "Please wait for the order  " + order.getNumber() + "to be approved");
                notificationService.save(distributorNotification);
                order.getCreatedBy().getNotifications().add(distributorNotification);

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
    public Order reject(Order order) {
        if (order.getStatus() != OrderStatus.SUBMITTED) {
            throw new RuntimeException("Can only reject submitted orders");
        }
        
        order.setStatus(OrderStatus.REJECTED);
        
        // Create notification
        createOrderNotifications(order, "Order Rejected");
        
        return orderRepository.save(order);
    }

    @Override
    public String sendReminder(User sender, User receiver, Order order) {



        String timestamp = LocalDateTime.now().toString();
        String orderNumber = order.getNumber();

        // HTML email content
        String htmlBody = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; color: #333; background-color: #f4f4f4; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .header { text-align: center; background-color: #007bff; color: #fff; padding: 10px; border-radius: 8px 8px 0 0; }
                    .header h1 { margin: 0; font-size: 24px; }
                    .content { padding: 20px; }
                    .quote { font-style: italic; font-size: 18px; color: #007bff; border-left: 4px solid #007bff; padding-left: 15px; margin: 20px 0; }
                    .greeting { font-size: 16px; line-height: 1.6; }
                    .signature { font-size: 14px; color: #555; margin-top: 20px; }
                    .footer { text-align: center; font-size: 12px; color: #777; margin-top: 20px; border-top: 1px solid #eee; padding-top: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>GGM-FMS</h1>
                    </div>
                    <div class="content">
                        <p class="greeting">Dear Customer,</p>
                        <p class="greeting">Welcome to GGM.</p>
                        <p class="quote">Your order with number: "%s" have reached overdue</p>
                        <p class="quote">Pay ASAP to avoid additional costs</p>
                
                        <p class="signature">Warm regards,<br>The GGM Team</p>
                    </div>
                    <div class="footer">
                        <p>Sent at: %s | <a href="#">Unsubscribe</a></p>
                        <p>Contact us at: <a href="mailto:support@ggm.com">support@ggm.com</a></p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(orderNumber,timestamp);
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo("ntwaliisimbivieira@gmail.com"); //receiver.getEmail()
            helper.setSubject("Order due-date updates");
            helper.setFrom("klgwnboy@gmail.com"); // sender.getEmail()
            helper.setText(htmlBody, true); // true indicates HTML content
            mailSender.send(message);
            System.out.println("Email sent");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());

        }
        return "Email service is working";
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