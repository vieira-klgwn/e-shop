package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.AuditLog;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.model.User;
import vector.StockManagement.repositories.AuditoryLogRepository;
import vector.StockManagement.services.AuditLogService;
import vector.StockManagement.services.AuditoryLogService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoryLogServiceImpl implements AuditoryLogService, AuditLogService {
    private final AuditoryLogRepository auditLogRepository;


    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }

    @Override
    public AuditLog findById(Long id) {
        return auditLogRepository.findById(id).orElse(null);
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    @Override
    public void delete(Long id) {
        auditLogRepository.deleteById(id);
    }
    
    @Override
    public void logEntityChange(String entityType, Long entityId, String operation, String oldValues, String newValues) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOperation(operation);
        auditLog.setOldValues(oldValues);
        auditLog.setNewValues(newValues);
        auditLog.setTimestamp(LocalDateTime.now());
        
        // Set user and tenant from security context
        setAuditContext(auditLog);
        
        auditLogRepository.save(auditLog);
    }

    @Override
    public void logUserAction(String action, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setOperation("USER_ACTION");
        auditLog.setEntityType("USER");
        auditLog.setNewValues(action + ": " + details);
        auditLog.setTimestamp(LocalDateTime.now());
        
        setAuditContext(auditLog);
        
        auditLogRepository.save(auditLog);
    }

    @Override
    public void logSystemEvent(String eventType, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setOperation("SYSTEM_EVENT");
        auditLog.setEntityType(eventType);
        auditLog.setNewValues(description);
        auditLog.setTimestamp(LocalDateTime.now());
        
        setAuditContext(auditLog);
        
        auditLogRepository.save(auditLog);
    }
    
    private void setAuditContext(AuditLog auditLog) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof User user) {
                auditLog.setUserId(user.getId());
                auditLog.setUserName(user.getFirstname() + " " + user.getLastname());
                if (user.getTenant() != null) {
                    auditLog.setTenant(user.getTenant());
                }
            }
        } catch (Exception e) {
            // If we can't get security context, continue without user info
        }
    }
}
