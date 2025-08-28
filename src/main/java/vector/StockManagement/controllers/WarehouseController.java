package vector.StockManagement.controllers;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Warehouse;
import vector.StockManagement.services.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public List<Warehouse> getAll() {
        return warehouseService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> getById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.findById(id);
        return warehouse != null ? ResponseEntity.ok(warehouse) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Warehouse create(@RequestBody Warehouse warehouse) {
        return warehouseService.save(warehouse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Warehouse> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        Warehouse existing = warehouseService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        warehouse.setIsActive(existing.getIsActive());
        //add more updates here
        return ResponseEntity.ok(warehouseService.save(warehouse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}