package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Inventory;
import vector.StockManagement.model.dto.UpdateInventoryDTO;
import vector.StockManagement.services.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<Inventory> getAll() {
        return inventoryService.findAll();
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Inventory> findByProductId(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findInventoryByProduct(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getById(@PathVariable Long id) {
        Inventory inventory = inventoryService.findInventoryByProduct(id);
        return inventory != null ? ResponseEntity.ok(inventory) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Inventory> create(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.save(inventory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> update(@PathVariable Long id, @RequestBody Inventory inventory) {
        Inventory existing = inventoryService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.setQtyOnHand(inventory.getQtyOnHand());
        //add more updates here
        return ResponseEntity.ok(inventoryService.save(inventory));
    }

    @PutMapping("/updateInventory/{id}")
    @PreAuthorize("hasRole('STORE_MANAGER','WAREHOUSE_MANAGER')")
    public ResponseEntity<Inventory> updateQtyOnHand(@PathVariable Long id, @RequestBody UpdateInventoryDTO request) {
        return ResponseEntity.ok(inventoryService.updateQtyOnHand(id, request.getQtyOnHand()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}