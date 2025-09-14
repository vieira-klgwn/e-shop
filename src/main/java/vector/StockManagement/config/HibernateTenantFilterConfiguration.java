package vector.StockManagement.config;

import jakarta.persistence.EntityManager;
import jakarta.servlet.Filter;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class HibernateTenantFilterConfiguration {

    private final EntityManager entityManager;

    public HibernateTenantFilterConfiguration(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void enableTenantFilter() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID is null; cannot apply tenant filter");
        }
        Session session = entityManager.unwrap(Session.class);
        var filter = session.enableFilter("tenantFilter");
        filter.setParameter("tenantId", tenantId);
    }
}
