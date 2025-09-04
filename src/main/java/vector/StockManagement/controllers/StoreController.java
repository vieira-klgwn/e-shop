package vector.StockManagement.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Store;
import vector.StockManagement.services.StoreService;

import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping
    public List<Store> getAll() {
        return storeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Store> getById(@PathVariable Long id) {
        Store store = storeService.findById(id);
        return store != null ? ResponseEntity.ok(store) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WAREHOUSE_MANAGER')")
    public Store create(@RequestBody Store store) {
        return storeService.save(store);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Store> update(@PathVariable Long id, @RequestBody Store store) {
        Store existing = storeService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        store.setManager(existing.getManager());
        //add more updates here
        return ResponseEntity.ok(storeService.save(store));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}