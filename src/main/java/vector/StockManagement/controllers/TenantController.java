package vector.StockManagement.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.auth.AuthenticationResponse;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.model.dto.TenantDTO;
import vector.StockManagement.services.TenantService;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
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

    @PostMapping("/admin")
    public ResponseEntity<AuthenticationResponse> create(@RequestBody TenantDTO tenantdto) {
        return ResponseEntity.ok(tenantService.save(tenantdto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> update(@PathVariable Long id, @RequestBody Tenant tenant) {
        return ResponseEntity.ok(tenantService.update(id, tenant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}