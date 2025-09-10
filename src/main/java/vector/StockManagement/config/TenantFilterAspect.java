package vector.StockManagement.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

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
        config.enableTenantFilter();
    }
}
