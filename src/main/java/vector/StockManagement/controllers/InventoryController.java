package vector.StockManagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Inventory;
import vector.StockManagement.services.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<Inventory> getAll() {
        return inventoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getById(@PathVariable Long id) {
        Inventory inventory = inventoryService.findById(id);
        return inventory != null ? ResponseEntity.ok(inventory) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Inventory create(@RequestBody Inventory inventory) {
        return inventoryService.save(inventory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> update(@PathVariable Long id, @RequestBody Inventory inventory) {
        Inventory existing = inventoryService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        inventory.setAvgUnitCost(existing.getAvgUnitCost());
        //add more updates here
        return ResponseEntity.ok(inventoryService.save(inventory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}