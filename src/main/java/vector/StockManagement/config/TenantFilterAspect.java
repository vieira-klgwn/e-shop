package vector.StockManagement.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vector.StockManagement.auth.AuthenticationController;

@Aspect
@Component
public class TenantFilterAspect {
    private static final Logger logger = LoggerFactory.getLogger(TenantFilterAspect.class);

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
            if (requestUri.contains("/api/tenants") || requestUri.startsWith("/api/auth/") || requestUri.contains("super_user/login") || requestUri.contains("/api/users")) {
                return; // Skip applying the tenant filter
            }

        }

        config.enableTenantFilter();
    }
}
