package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Order;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.OrderDTO;
import vector.StockManagement.model.enums.OrderStatus;
import vector.StockManagement.services.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<Order> create(@AuthenticationPrincipal User user, @RequestBody OrderDTO orderDTO) {

        return ResponseEntity.ok(orderService.save(user.getId(),orderDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody OrderDTO orderdto) {
        //add more updates here
        return ResponseEntity.ok(orderService.update(id,orderdto));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Order> approve(@PathVariable Long id) {

        Order order = orderService.findById(id);
        if (order == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(orderService.approve(order));

    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Order> reject(@PathVariable Long id) {
        Order order = orderService.findById(id);
        if (order == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(orderService.reject(order));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}