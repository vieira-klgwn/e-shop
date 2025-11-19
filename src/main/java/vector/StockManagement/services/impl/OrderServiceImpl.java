package vector.StockManagement.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
//import vector.StockManagement.config.TenantContext;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.*;
import vector.StockManagement.model.enums.*;
import vector.StockManagement.repositories.*;
import vector.StockManagement.services.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
    private final ProductSizeRepository productSizeRepository;
    private final ProductSizeService productSizeService;
    private static final Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
    private final OrderedProductSizeRepository orderedProductSizeRepository;


    @Override
    public List<OrderDisplayDTO> findAll() {

        List<OrderDisplayDTO> displayDTOs = orderRepository.findAll().stream().map(this::getOrderDisplayDTO).toList();

        List<OrderDisplayDTO> displays = new ArrayList<>();
        for (OrderDisplayDTO displayDTO : displayDTOs) {

            displays.add(displayDTO);
        }
        return displays;
    }

    @Override
    public List<OrderDisplayDTO> findAllByDistributor(Long distributorId) {
        User user = userRepository.findById(distributorId).orElseThrow(() -> new RuntimeException("User not found"));


        return orderRepository.findAllByCreatedBy(user).stream().map(this::getOrderDisplayDTO).toList();
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


        // Force-load lazy collections to avoid empty proxies
        Hibernate.initialize(order.getOrderLines());
        for (OrderLine line : order.getOrderLines()) {
            Hibernate.initialize(line.getProductSizes());
        }

        List<OrderDisplayDTO.OrderLineDTO> lineDTOS = new ArrayList<>();

        for(OrderLine line: order.getOrderLines()) {

            OrderDisplayDTO.OrderLineDTO lineDTO = getOrderLineDTO(order, line);
            lineDTOS.add(lineDTO);
        }
        orderDisplayDTO.setOrderLines(lineDTOS);
        return orderDisplayDTO;
    }

    public OrderDisplayDTO.OrderLineDTO getOrderLineDTO(Order order, OrderLine line) {

        List<OrderedProductSize> sizes = line.getProductSizes();


        List<OrderProductSizeDTO> orderProductSizesDTO = new ArrayList<>();


        Long total = 0L;
        for (OrderedProductSize size : sizes) {


            OrderProductSizeDTO orderProductSizeDTO = new OrderProductSizeDTO();
            orderProductSizeDTO.setProductSize(size.getSize());
            orderProductSizeDTO.setProductSizeId(size.getId());
            orderProductSizeDTO.setQuantityOrdered(size.getQuantityInStock().longValue());
            orderProductSizeDTO.setQtyOnHand(size.getQuantityInStock());
            orderProductSizeDTO.setPrice(size.getPrice());
            orderProductSizesDTO.add(orderProductSizeDTO);

            total += size.getQuantityInStock() * size.getPrice();


        }
        OrderDisplayDTO.OrderLineDTO lineDTO = new OrderDisplayDTO.OrderLineDTO();
        lineDTO.setProductName(line.getProduct().getName());
        lineDTO.setLineTotal(total);
        lineDTO.setProductSizes(orderProductSizesDTO);
        lineDTO.setPrice(total);
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

    // I am dong the wrong thing when I use the "quantity ordered thing...when a new order is made , the value of quantityOrdered is overriden by that current order's value"
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
        order.setSeller(user.getDistributor());
        order.setCurrency("FRW");

//        OrderLevel level = user.getRole().equals(Role.RETAILER) ? OrderLevel.L1 : OrderLevel.L1;


        order.setLevel(OrderLevel.L1);




///  here you can only register an order if you have an orderline and an order line should also have a product,
        Order savedOrder = orderRepository.saveAndFlush(order);


//        LocationType sourceLocation = (level == OrderLevel.L1) ? LocationType.DISTRIBUTOR : LocationType.RETAILER;
//        PriceListLevel priceLevel = (level == OrderLevel.L1) ? PriceListLevel.DISTRIBUTOR : PriceListLevel.DISTRIBUTOR;

        
        // Create order lines and calculate totals
        Long totalAmount = 0L;
        if (orderDto.getOrderLines() != null && !orderDto.getOrderLines().isEmpty()) {
            for (OrderDTO.OrderLineDTO lineDto: orderDto.getOrderLines()) {
                Product product = productRepository.findById(lineDto.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

//                Inventory inventory = inventoryRepository.findByProductAndLocationType(product, sourceLocation);
//                if (!inventoryService.hasSufficientStock(product, lineDto.getQty(), sourceLocation)) {
//                    throw new RuntimeException("Insufficient stock for product: " + product.getSku() + " at " + sourceLocation + " and its location is " + sourceLocation ) ;
//                }




                lineDto.getSizes().forEach((key, value) -> updateProductBySizeAfterOrder(product, key, value));

//                if (ps == null || ps.getQuantityInStock() < lineDto.getQty()) {
//                    throw new RuntimeException("Insufficient stock for " + product.getName() + " with size " + lineDto.getSize());
//                }


//                // Get price based on level
//
////                Long price = getProductPrice(product, priceLevel);
//                Long price = null;
//                if (order.getLevel() == OrderLevel.L1) {
//
//                    price = product.getFactoryPrice();
//                }
//                else {
//                    price= product.getDistributorPrice();
//                }
//
//
//
//
//
//                if (price == -1L) {
//                    throw new RuntimeException("No price found for product: " + product.getSku() + " at level " + priceLevel);
//                }

                OrderLine orderLine = new OrderLine();

//                if (lineDto.getQty() <= 0){
//                    throw new RuntimeException("Order quantity should be a positive number and not zero");
//                }

//                orderLine.setUnitPrice(ps.getPrice());

                //price for a whole saler
                User user1 = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found (buyer)"));

//                if (user1.getRole() == Role.WHOLE_SALER){
//                    orderLine.setUnitPrice(ps.getPrice()-500L);
//                }
//                orderLine.setProductSize(ps.getSize());
                orderLine.setOrder(savedOrder);
                orderLine.setProduct(product);
//                orderLine.setQty(lineDto.getQty());
                List<Long> totals = new ArrayList<>();
                lineDto.getSizes().forEach((sizeKey, value) -> {
                    ProductSize size = productSizeRepository.findByProductAndSize(product,sizeKey);

                    //Create whole_saler's stock
                    OrderedProductSize orderedProductSize = new OrderedProductSize();
                    orderedProductSize.setQuantityInStock(value);
                    orderedProductSize.setPrice(size.getPrice());
                    orderedProductSize.setOrderLine(orderLine);
                    orderedProductSize.setProduct(product);
                    orderedProductSize.setCustomer(user1);
                    orderedProductSizeRepository.save(orderedProductSize);
                    user1.getProductsOrdered().add(orderedProductSize);
                    userRepository.save(user1);



                    if (orderLine.getProductSizes() == null){
                        orderLine.setProductSizes(new ArrayList<>());
                    }
                    orderLine.getProductSizes().add(orderedProductSize);
                    orderedProductSize.setOrderLine(orderLine);
                    orderedProductSizeRepository.save(orderedProductSize);
                    orderLine.getProductSizes().add(orderedProductSize);
                    size.setOrderLine(orderLine);
                    Long unitPrice = 0L;

                    if (user.getRole() ==Role.RETAILER){
                        unitPrice = size.getPrice()-500;
                    }
                    else {
                        unitPrice = size.getPrice();
                    }
                    totals.add(unitPrice * value);
                    size.setQuantityOrdered(value.longValue());
                    orderLineRepository.save(orderLine);
                    productSizeRepository.save(size);


                });
                for(Long total: totals){

                    if (orderLine.getLineTotal()==null){
                        orderLine.setLineTotal(0L);
                    }
                    orderLine.setLineTotal(orderLine.getLineTotal() + total);
                }
                orderLine.setTenant(tenant);
                orderLineRepository.save(orderLine);
                totalAmount += orderLine.getLineTotal();
                savedOrder.getOrderLines().add(orderLine);
                orderRepository.save(savedOrder);
//                inventoryService.reserveStock(product,lineDto.getQty(),sourceLocation); this was causing errors
            }

        }

        
        savedOrder.setOrderAmount(totalAmount);
        savedOrder.setStatus(OrderStatus.DRAFT);
        orderRepository.save(savedOrder);
        return savedOrder;
    }

    public ProductSize updateProductBySizeAfterOrder(Product product, String productSize, Integer quantity) {
        ProductSize size = productSizeRepository.findByProductAndSize(product, productSize);
        if (size == null || size.getQuantityInStock() < quantity) {
            throw new RuntimeException("Insufficient stock for " + product.getName() + " with size " + productSize);
        }
        size.setQuantityInStock(size.getQuantityInStock()- quantity);
        return productSizeRepository.save(size);
    }


    private ProductSize updateProductBySizeAfterOrderRejection(Product product, String productSize, Integer quantity) {
        ProductSize size = productSizeRepository.findByProductAndSize(product, productSize);
        if (size == null) {
            throw new RuntimeException("Oops, Size for this order might not exist!");
        }
        size.setQuantityInStock(size.getQuantityInStock()+ quantity);
        return productSizeRepository.save(size);
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




    // In your OrderServiceImpl class
    @Override
    @Transactional  // Ensures single transaction; flushes only at commit (avoids mid-iteration flushes)
    public Order adjustOrder(Long id, AdjustOrderDTO adjustOrderDTO, Boolean isAllowedToAdjust) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        boolean isAdjusted = false;
        boolean quantityIsChanged = false;

        // Partial Quantity Block (apply first, as it changes quantities used in price calcs)
        if (adjustOrderDTO.getPartialQtys() != null && !adjustOrderDTO.getPartialQtys().isEmpty()) {
            quantityIsChanged = true;
            Map<Long, Long> partialQtys = adjustOrderDTO.getPartialQtys();
            for (Map.Entry<Long, Long> partialQty : partialQtys.entrySet()) {
                if (partialQty.getValue() < 0) {
                    throw new RuntimeException("Quantity cannot be negative: " + partialQty.getValue());
                }
                // Find and update the specific size (no full loop over all lines per partial for efficiency)
                boolean found = false;
                for (OrderLine line : order.getOrderLines()) {  // Direct iteration safe now (no mid-saves)
                    for (OrderedProductSize size : line.getProductSizes()) {  // Direct iteration
                        if (size.getId().equals(partialQty.getKey())) {
                            if (partialQty.getValue() > size.getQuantityInStock()) {
                                throw new RuntimeException("The updated quantity you want to add, is greater than the ordered quantity before");
                            }
                            // Note: This adds the difference to stock (assuming partialQty is the new total qty, so delta = new - old)


                            // NO saveAndFlush here—defer to end via cascade
                            size.setQuantityInStock(size.getQuantityInStock() - partialQty.getValue().intValue());
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }
                if (!found) {
                    // Optional: Log warning - partial for non-existent size
                }
            }
        }

        // Price Adjustments Block (apply after partials, using updated qtys)
        Map<Long, Long> adjustments = null;
        if (adjustOrderDTO.getProductPriceAdjustments() != null && !adjustOrderDTO.getProductPriceAdjustments().isEmpty() && isAllowedToAdjust) {
            isAdjusted = true;
            adjustments = adjustOrderDTO.getProductPriceAdjustments();
        }

        // Recalculate ALL lineTotals based on current qtys and prices (adjusted if applicable)
        // No zeroing or saves in loops—sum contributions from all sizes
        for (OrderLine line : order.getOrderLines()) {  // Direct iteration safe
            Long calculatedTotal = 0L;
            for (OrderedProductSize size : line.getProductSizes()) {  // Direct iteration
                Long priceToUse = size.getPrice() != null ? size.getPrice() : 0L;
                Long qtyToUse = size.getQuantityInStock() != null ? size.getQuantityInStock() : 0L;

                // If price adjustment for this size, override price
                if (adjustments != null && adjustments.containsKey(size.getId())) {
                    priceToUse = adjustments.get(size.getId());
                }

                calculatedTotal += priceToUse * qtyToUse;
            }
            line.setLineTotal(calculatedTotal);
            // NO saveAndFlush here—defer
        }

        // Final: Recalc orderAmount from all lineTotals (simple sum)
        Long totalAmount = order.getOrderLines().stream()
                .mapToLong(line -> line.getLineTotal() != null ? line.getLineTotal() : 0L)
                .sum();

        // Apply overall adjustments if present (e.g., customerDiscount or priceAdjustment)
        // Assuming these are absolute deductions/additions to total
        Long finalAmount = totalAmount;
        if (adjustOrderDTO.getCustomerDiscount() != null) {
            finalAmount -= adjustOrderDTO.getCustomerDiscount();
        }
        if (adjustOrderDTO.getPriceAdjustment() != null) {
            finalAmount += adjustOrderDTO.getPriceAdjustment();  // Or -= if it's a discount
        }
        order.setOrderAmount(finalAmount < 0 ? 0L : finalAmount);  // Clamp to >=0

        if (isAdjusted) {
            order.setStatus(OrderStatus.PRICE_ADJUSTED);
        } else if (quantityIsChanged) {
            order.setStatus(OrderStatus.QUANTITY_ADJUSTED);

        }

        // Single save at end: Cascades to OrderLines/ProductSizes (no intermediate flushes)
        return orderRepository.save(order);  // This triggers one flush/commit for all changes
    }
    
    @Transactional
    public Order fulfillOrder(Long orderId, Long userId) {
        Order order = findById(orderId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (order.getStatus() != OrderStatus.APPROVED_BY_ACCOUNTANT) {
            throw new RuntimeException("Can only fulfill approved orders");
        }


        order.setStatus(OrderStatus.FULFILLED);
        for(OrderLine line: order.getOrderLines()){
            for (OrderedProductSize size: line.getProductSizes()){
                size.setIsFulfilled(true);
                orderedProductSizeRepository.save(size);
            }
        }
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


    public Order approveByStoreManager(Long userId, Order order){

        User approver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        if (order.getStatus() != OrderStatus.SUBMITTED) {
            throw new RuntimeException("Cannot approve order that is not in SUBMITTED status");
        }


        order.setStatus(OrderStatus.APPROVED_BY_STORE_MANAGER);
        order.setApprovedBy(approver);
        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public Order approve(Long userId, Order order) {

        User approver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        if (!(order.getStatus()==OrderStatus.APPROVED_BY_STORE_MANAGER || order.getStatus() == OrderStatus.QUANTITY_ADJUSTED || order.getStatus() == OrderStatus.PRICE_ADJUSTED)) {
            throw new RuntimeException("Order cannot be approved by accountant without approval from store manager " + order.getStatus());
        }

        // Generate invoice
        Invoice invoice = createInvoiceFromOrder(order, approver);
        invoiceRepository.save(invoice);
        
        // Send notifications
        createOrderNotifications(order, "Order Approved");
        order.setStatus(OrderStatus.APPROVED_BY_ACCOUNTANT);
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

        for (OrderLine orderLine : order.getOrderLines()) {

            for(OrderedProductSize size1: orderLine.getProductSizes()){

                updateProductBySizeAfterOrderRejection(orderLine.getProduct(),size1.getSize(),size1.getQuantityInStock());
            }


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

    @Override
    public Order allowAdjustPrice(User sender, Order order, AdjustOrderDTO adjustOrderDTO) {
        adjustOrder(order.getId(), adjustOrderDTO,Boolean.TRUE );
        return order;
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