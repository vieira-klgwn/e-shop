package vector.StockManagement.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class TenantFilterAspect {

    private final HibernateTenantFilterConfiguration config;

    public TenantFilterAspect(HibernateTenantFilterConfiguration config) {
        this.config = config;
    }

    // Enable filter before any repository method is executed
    @Before("execution(* org.springframework.data.repository.Repository+.*(..))")
    public void applyTenantFilter() {

        // Check if the current request is for /api/tenants/admin
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String requestUri = attributes.getRequest().getRequestURI();
            if ("/api/tenants/admin".equals(requestUri)) {
                return; // Skip applying the tenant filter
            }
        }

        config.enableTenantFilter();
    }
}
