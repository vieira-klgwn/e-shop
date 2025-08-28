package vector.StockManagement.services;

import vector.StockManagement.model.AuditLog;

import java.util.List;

public interface AuditoryLogService {
    List<AuditLog> findAll();
    AuditLog findById(Long id);
    AuditLog save(AuditLog auditLog);
    void delete(Long id);
}