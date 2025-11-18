package vector.StockManagement.controllers;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.OrderedProductSize;
import vector.StockManagement.services.OrderedProductSizeService;

import java.util.List;

@RestController
@RequestMapping("/api/customerStock")
@RequiredArgsConstructor
public class OrderedProductSizeController {
    private final OrderedProductSizeService orderedProductSizeService;



    @PostMapping
    private ResponseEntity<OrderedProductSize> createOrderedProductSize(OrderedProductSize orderedProductSize) {
        return ResponseEntity.ok(orderedProductSizeService.createOrderedProductSize(orderedProductSize));
    }

    @GetMapping
    private ResponseEntity<List<OrderedProductSize>> findOrderedProductSizes() {
        return ResponseEntity.ok(orderedProductSizeService.findAllOrderedProductSizes());
    }

    @GetMapping("/{id}")
    private ResponseEntity<OrderedProductSize> findOrderedProductSizeById(@PathVariable Long id) {
        return ResponseEntity.ok(orderedProductSizeService.findOrderedProductSizeById(id));
    }

    @GetMapping("/user/{id}")
    private ResponseEntity<List<OrderedProductSize>> findOrderedProductSizeByCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(orderedProductSizeService.findOrderedProductSizeByCustomer(id));
    }
}
