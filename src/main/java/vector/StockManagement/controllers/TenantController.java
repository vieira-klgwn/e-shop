package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.services.TenantService;

import java.util.List;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public List<Tenant> getAll() {
        return tenantService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getById(@PathVariable Long id) {
        Tenant tenant = tenantService.findById(id);
        return tenant != null ? ResponseEntity.ok(tenant) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Tenant create(@RequestBody Tenant tenant) {
        return tenantService.save(tenant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> update(@PathVariable Long id, @RequestBody Tenant tenant) {
        Tenant existing = tenantService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        tenant.setSettings(existing.getSettings());
        //add more updates here
        return ResponseEntity.ok(tenantService.save(tenant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}