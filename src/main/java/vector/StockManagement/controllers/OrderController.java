package vector.StockManagement.controllers;

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
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.OrderDTO;
import vector.StockManagement.model.dto.OrderDisplayDTO;
import vector.StockManagement.model.enums.OrderLevel;
import vector.StockManagement.model.enums.OrderStatus;
import vector.StockManagement.repositories.OrderRepository;
import vector.StockManagement.repositories.UserRepository;
import vector.StockManagement.services.OrderService;
import vector.StockManagement.services.impl.OrderServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderServiceImpl orderServiceImpl;
    private final OrderRepository orderRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','ACCOUNTANT','WAREHOUSE_MANAGER','ADMIN','SALES_MANAGER','STORE_MANAGER')")
    public Page<OrderDisplayDTO> getAll(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.findAll();
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());
    }

    @GetMapping("/distributor")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','ACCOUNTANT','WAREHOUSE_MANAGER','ADMIN','SALES_MANAGER','STORE_MANAGER')")
    public Page<OrderDisplayDTO> getAllByDistributor(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size, @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.findAllByDistributor(currentUser.getId());
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());
    }

    @GetMapping("/store_ordersToApprove") // accountant uses this api to get all orders from the retailer
    @PreAuthorize("hasAnyRole('ACCOUNTANT_AT_STORE')")
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
        List<OrderDisplayDTO> orders = orderService.getOrdersFromRetailer(currentUser.getId()).stream().filter(order-> OrderStatus.valueOf(order.getOrderStatus())== OrderStatus.APPROVED).toList();
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());

    }




    @GetMapping("/retailer/store_orders")  //use this api to see all orders from the store--retailer uses it to see all orders he/she made---distributor is also using it to see all retailer orders
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','STORE_MANAGER','RETAILER')")
    public Page<OrderDisplayDTO> getAllStoreOrders(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "0") int size, @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.getOrderDisplayDTOforStore(currentUser.getDistributor().getId());
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());

    }

    @GetMapping("/store_orders")  //use this api to see all orders from the store--retailer uses it to see all orders he/she made---distributor is also using it to see all retailer orders
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','STORE_MANAGER','RETAILER')")
    public Page<OrderDisplayDTO> getAllStoreOrdersForDistributor(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "0") int size, @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        List<OrderDisplayDTO> orders = orderService.getOrderDisplayDTOforStoreForDistributor();
        int start = Math.min((int) pageable.getOffset(), orders.size());
        int end = Math.min(start + pageable.getPageSize(), orders.size());
        return new PageImpl<>(orders.subList(start, end), pageable, orders.size());

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DISTRIBUTOR', 'STORE_MANAGER', 'SALES_MANAGER', 'ACCOUNTANT','WAREHOUSE_MANAGER','ACCOUNTANT_AT_STORE')")
    public ResponseEntity<OrderDisplayDTO> getById(@PathVariable Long id) {
        return  ResponseEntity.ok(orderService.findByIdDisplayed(id));
    }

    @PostMapping("/send-reminder/{orderId}")
    public ResponseEntity<String> sendReminder(@AuthenticationPrincipal User currentUser, @PathVariable Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id: "+ orderId+ " is not found"));
        return ResponseEntity.ok(orderService.sendReminder(currentUser, order.getCreatedBy(),order));
    }





    @PostMapping
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','STORE_MANAGER','RETAILER')")
    public ResponseEntity<Order> create(@AuthenticationPrincipal User user, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.save(user.getId(), orderDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody OrderDTO orderdto) {
        return ResponseEntity.ok(orderService.update(id, orderdto));
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('ACCOUNTANT', 'ACCOUNTANT_AT_STORE')")
    public ResponseEntity<Order> approve(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Order order = orderRepository.getOrderById(id);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        return ResponseEntity.ok(orderServiceImpl.approve(user.getId(),order ));
    }

    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAnyRole('ACCOUNTANT','ACCOUNTANT_AT_STORE')")
    public ResponseEntity<Order> reject(@PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(orderService.reject(order));
    }

    @PutMapping("/submit/{id}")
    @PreAuthorize("hasAnyRole('DISTRIBUTOR','RETAILER')")
    public ResponseEntity<Order> submit(@AuthenticationPrincipal User user, @PathVariable Long id) {
        // keep compatibility by calling service method via update flow in implementation
        return ResponseEntity.ok(((vector.StockManagement.services.impl.OrderServiceImpl) orderService).submitOrder(id, user.getId()));
    }

    @PutMapping("/fulfill/{id}")
    @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER','STORE_MANAGER')")
    public ResponseEntity<Order> fulfill(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(((vector.StockManagement.services.impl.OrderServiceImpl) orderService).fulfillOrder(id, user.getId()));
    }

    @PutMapping("/store/fulfill/{id}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER')")
    public ResponseEntity<Order> fulfillRetailOrders(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(((vector.StockManagement.services.impl.OrderServiceImpl) orderService).fulfillOrder(id, user.getId()));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
