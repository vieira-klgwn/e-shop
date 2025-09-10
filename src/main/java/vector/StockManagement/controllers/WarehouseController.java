package vector.StockManagement.controllers;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.CreateWareHouseRequest;
import vector.StockManagement.model.Warehouse;
import vector.StockManagement.services.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("api/warehouses")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public List<Warehouse> getAll() {
        return warehouseService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Warehouse> getById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.findById(id);
        return warehouse != null ? ResponseEntity.ok(warehouse) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Warehouse> create(@RequestBody Warehouse warehouse) {
        return ResponseEntity.ok(warehouseService.save(warehouse));
    }


    //update API are not well programmed, take time to work on them
    @PutMapping("/{id}")
    public ResponseEntity<Warehouse> update(@RequestBody Warehouse warehouse, @PathVariable Long id) {
        Warehouse existing = warehouseService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
//        existing.setName(request.getName());
        //add more updates here
        return ResponseEntity.ok(warehouseService.save(warehouse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}