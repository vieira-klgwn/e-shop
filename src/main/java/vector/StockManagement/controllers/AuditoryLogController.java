package vector.StockManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vector.StockManagement.model.AuditLog;
import vector.StockManagement.service.AuditLogService;

import java.util.List;

@RestController
@RequestMapping("/auditlogs")
public class AuditoryLogController {

    @Autowired
    private AuditoryLogService auditLogService;

    @GetMapping
    public List<AuditLog> getAll() {
        return auditLogService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getById(@PathVariable Long id) {
        AuditLog auditLog = auditLogService.findById(id);
        return auditLog != null ? ResponseEntity.ok(auditLog) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public AuditLog create(@RequestBody AuditLog auditLog) {
        return auditLogService.save(auditLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditLog> update(@PathVariable Long id, @RequestBody AuditLog auditLog) {
        AuditLog existing = auditLogService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        auditLog.setId(id);
        return ResponseEntity.ok(auditLogService.save(auditLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auditLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}