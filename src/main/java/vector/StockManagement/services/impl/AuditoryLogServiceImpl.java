package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.AuditLog;
import vector.StockManagement.repositories.AuditoryLogRepository;
import vector.StockManagement.repositories.AuditoryLogRepository;
import vector.StockManagement.services.AuditoryLogService;
import vector.StockManagement.services.AuditoryLogService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoryLogServiceImpl implements AuditoryLogService {
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
}