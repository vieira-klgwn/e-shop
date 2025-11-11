package vector.StockManagement.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Order;
import vector.StockManagement.model.OrderLine;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.AdjustOrderDTO;
import vector.StockManagement.model.dto.OrderDTO;
import vector.StockManagement.model.dto.OrderDisplayDTO;
import vector.StockManagement.model.enums.ActivityCategory;
import vector.StockManagement.model.enums.OrderLevel;
import vector.StockManagement.model.enums.OrderStatus;
import vector.StockManagement.model.enums.Role;
import vector.StockManagement.repositories.AdjustOrderDTORepository;
import vector.StockManagement.repositories.OrderLineRepository;
import vector.StockManagement.repositories.OrderRepository;
import vector.StockManagement.repositories.UserRepository;
import vector.StockManagement.services.ActivityService;
import vector.StockManagement.services.AdjustHistoryService;
import vector.StockManagement.services.OrderService;
import vector.StockManagement.services.impl.OrderServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderServiceImpl orderServiceImpl;
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ActivityService activityService;
    private final AdjustOrderDTORepository adjustOrderDTORepository;
    private final AdjustHistoryService adjustHistoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','ACCOUNTANT','WAREHOUSE_MANAGER','ADMIN','SALES_MANAGER','STORE_MANAGER','MANAGING_DIRECTOR','WHOLE_SALER', 'RETAILER')")
    public Page<OrderDisplayDTO> getAll(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.findAll();
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());
    }

    @PostMapping("/{id}")
    public ResponseEntity<AdjustOrderDTO> createAdjustOrder(
            @Valid @RequestBody AdjustOrderDTO adjustOrderDTO, // @Valid triggers validation
            @PathVariable Long id) { // Assuming User is the principal type

        Order order = orderRepository.findById(id).orElseThrow(() ->new RuntimeException("Order not found"));
        // Optional: Basic validation (expand as needed, e.g., check maps for negatives)
        if (adjustOrderDTO.getPartialQtys() != null) {
            adjustOrderDTO.getPartialQtys().forEach((key, value) -> {
                if (value < 0) {
                    throw new IllegalArgumentException("Partial quantity cannot be negative: " + value);
                }
            });
        }

        // Create and populate the entity (copy all fields)
        AdjustOrderDTO savedDto = new AdjustOrderDTO();
        savedDto.setPartialQtys(adjustOrderDTO.getPartialQtys() != null ? new HashMap<>(adjustOrderDTO.getPartialQtys()) : new HashMap<>()); // Deep copy to avoid shared state
        savedDto.setProductPriceAdjustments(adjustOrderDTO.getProductPriceAdjustments() != null ? new HashMap<>(adjustOrderDTO.getProductPriceAdjustments()) : new HashMap<>()); // Deep copy
        savedDto.setOrder(order);


        // Optional: Associate with user (add 'private User createdBy;' to DTO with @ManyToOne)
        // savedDto.setCreatedBy(user);

        // Persist to DB
        AdjustOrderDTO persistedDto = adjustOrderDTORepository.save(savedDto);
        order.setAdjustOrderDTO(savedDto);
        orderRepository.save(order);
        // Return the saved DTO with generated ID
        return ResponseEntity.ok(persistedDto);
    }

    @PutMapping("/{id}/adjust")
    @Transactional
    public ResponseEntity<Order> adjustOrder(@PathVariable Long id){
        Order order = orderRepository.findById(id).orElseThrow(()-> new IllegalStateException("Order not found"));

        AdjustOrderDTO adjustOrderDTO = adjustOrderDTORepository.findByOrder(order);
        return ResponseEntity.ok(orderService.adjustOrder(id,adjustOrderDTO,Boolean.TRUE));

    }

    @PutMapping("/{id}/changeQuantity")
    public ResponseEntity<Order> changeOrderQuantity(@PathVariable Long id, @RequestBody AdjustOrderDTO adjustOrderDTO){
        return ResponseEntity.ok(orderService.adjustOrder(id, adjustOrderDTO,Boolean.TRUE));
    }

    @GetMapping("/own")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','ACCOUNTANT','WAREHOUSE_MANAGER','ADMIN','SALES_MANAGER','STORE_MANAGER','MANAGING_DIRECTOR','RETAILER', 'WHOLE_SALER')")
    public Page<OrderDisplayDTO> getAllByDistributor(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size, @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.findAllByDistributor(currentUser.getId());
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());
    }

    @GetMapping("/store_ordersToApprove") // accountant uses this api to get all orders from the retailer
    @PreAuthorize("hasAnyRole('ACCOUNTANT_AT_STORE','MANAGING_DIRECTOR')")
    public Page<OrderDisplayDTO> getStoreOrdersToApprove(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "0") int size, @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.getOrdersFromRetailer(currentUser.getId()).stream().filter(order-> OrderStatus.valueOf(order.getOrderStatus())==OrderStatus.SUBMITTED).toList();
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());

    }

    @GetMapping("/store_ordersToFulfill")
    @PreAuthorize("hasAnyRole('STORE_MANAGER')")
    public Page<OrderDisplayDTO> getStoreOrdersToFulfill(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "0") int size, @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.getOrdersFromRetailer(currentUser.getId()).stream().filter(order-> OrderStatus.valueOf(order.getOrderStatus())== OrderStatus.APPROVED_BY_ACCOUNTANT).toList();
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());

    }




    @GetMapping("/retailer/store_orders")  //use this api to see all orders from the store--retailer uses it to see all orders he/she made---distributor is also using it to see all retailer orders
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','STORE_MANAGER','RETAILER','MANAGING_DIRECTOR')")
    public Page<OrderDisplayDTO> getAllStoreOrders(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "0") int size, @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.getOrderDisplayDTOforStore(currentUser.getDistributor().getId());
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());

    }

    @GetMapping("/store_orders")  //use this api to see all orders from the store--retailer uses it to see all orders he/she made---distributor is also using it to see all retailer orders
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','STORE_MANAGER','RETAILER','MANAGING_DIRECTOR')")
    public Page<OrderDisplayDTO> getAllStoreOrdersForDistributor(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "0") int size, @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.getOrderDisplayDTOforStoreForDistributor();
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DISTRIBUTOR', 'STORE_MANAGER', 'SALES_MANAGER', 'ACCOUNTANT','WAREHOUSE_MANAGER','ACCOUNTANT_AT_STORE', 'RETAILER','MANAGING_DIRECTOR', 'WHOLE_SALER')")
    public ResponseEntity<OrderDisplayDTO> getById(@PathVariable Long id) {
        return  ResponseEntity.ok(orderService.findByIdDisplayed(id));
    }

    @PostMapping("/send-reminder/{orderId}")
    public ResponseEntity<String> sendReminder(@AuthenticationPrincipal User currentUser, @PathVariable Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id: "+ orderId+ " is not found"));
        return ResponseEntity.ok(orderService.sendReminder(currentUser, order.getCreatedBy(),order));
    }





    @PostMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','STORE_MANAGER','RETAILER','WHOLE_SALER')")
    public ResponseEntity<Order> create(@AuthenticationPrincipal User user, @RequestBody OrderDTO orderDTO) {

        Order order = orderService.save(user.getId(), orderDTO);
        activityService.createActivity(user, "Order Created", ActivityCategory.ORDERS,"Order created by user "+ user.getEmail());
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody OrderDTO orderdto) {
        return ResponseEntity.ok(orderService.update(id, orderdto));
    }

    @PutMapping("/store_manager/approve/{id}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER')")
    public ResponseEntity<Order> approveByStoreManager(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Order order = orderRepository.getOrderById(id);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        activityService.createActivity(user, "Order Approved", ActivityCategory.ORDERS,"Order approved by store manager "+ user.getEmail());
        Order order1 = orderServiceImpl.approveByStoreManager(user.getId(),order );
        return ResponseEntity.ok(order1);
    }

    @PutMapping("/accountant/approve/{id}")
    @PreAuthorize("hasAnyRole('ACCOUNTANT', 'ACCOUNTANT_AT_STORE')")
    public ResponseEntity<Order> approveByAccountant(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Order order = orderRepository.getOrderById(id);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        Order order1 = orderService.approve(user.getId(), order);
        activityService.createActivity(user, "Order Approved", ActivityCategory.ORDERS,"Order approved by accountant"+ user.getEmail());

        return ResponseEntity.ok(order1);
    }

    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAnyRole('ACCOUNTANT','ACCOUNTANT_AT_STORE','STORE_MANAGER')")
    public ResponseEntity<Order> reject(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order == null) return ResponseEntity.notFound().build();
        Order order1 = orderService.reject(order);
        activityService.createActivity(user, "Order Rejected", ActivityCategory.ORDERS,"Order rejected by user "+ user.getEmail());
        return ResponseEntity.ok(order1);
    }

    @PutMapping("/submit/{id}")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','RETAILER','WHOLE_SALER','ACCOUNTANT')")
    public ResponseEntity<Order> submit(@AuthenticationPrincipal User user, @PathVariable Long id) {
        // keep compatibility by calling service method via update flow in implementation
        Order order =((vector.StockManagement.services.impl.OrderServiceImpl) orderService).submitOrder(id, user.getId());
        activityService.createActivity(user, "Order Submitted", ActivityCategory.ORDERS,"Order submitted by user "+ user.getEmail());
        return ResponseEntity.ok(order);
    }

    @PutMapping("/fulfill/{id}")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER','STORE_MANAGER')")
    public ResponseEntity<Order> fulfill(@AuthenticationPrincipal User user, @PathVariable Long id) {
        activityService.createActivity(user, "Order Fulfilled", ActivityCategory.ORDERS,"Order fulfilled by store manager: "+ user.getEmail());
        Order order = ((vector.StockManagement.services.impl.OrderServiceImpl) orderService).fulfillOrder(id, user.getId());
        return ResponseEntity.ok(order);
    }

    @PutMapping("/store/fulfill/{id}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER')")
    public ResponseEntity<Order> fulfillRetailOrders(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(((vector.StockManagement.services.impl.OrderServiceImpl) orderService).fulfillOrder(id, user.getId()));
    }

    @GetMapping("/adjustmentObject/{id}")
    public ResponseEntity<AdjustOrderDTO> getAdjustmentObject(@PathVariable Long id) {
        return ResponseEntity.ok(adjustOrderDTORepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found")));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
