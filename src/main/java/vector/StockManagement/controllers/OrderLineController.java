package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.OrderLine;
import vector.StockManagement.services.OrderLineService;

import java.util.List;

@RestController
@RequestMapping("/api/orderlines")
@RequiredArgsConstructor
public class OrderLineController {

    private final OrderLineService orderLineService;

    @GetMapping
    public List<OrderLine> getAll() {
        return orderLineService.findAll();
    }



    @GetMapping("/{id}")
    public ResponseEntity<OrderLine> getById(@PathVariable Long id) {
        OrderLine orderLine = orderLineService.findById(id);
        return orderLine != null ? ResponseEntity.ok(orderLine) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<OrderLine> createOrderlineByOrderId(@PathVariable Long id, @RequestBody OrderLine orderLine) {

        return ResponseEntity.ok(orderLineService.createOrderlineByOrder(id, orderLine));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderLine> update(@PathVariable Long id, @RequestBody OrderLine orderLine) {
        OrderLine existing = orderLineService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        orderLine.setTax(existing.getTax());
        return ResponseEntity.ok(orderLineService.save(orderLine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderLineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}