package vector.StockManagement.services;

import vector.StockManagement.model.AuditLog;

public interface AuditLogService {
    void logEntityChange(String entityType, Long entityId, String operation, String oldValues, String newValues);
    void logUserAction(String action, String details);
    void logSystemEvent(String eventType, String description);
}
