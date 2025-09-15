package vector.StockManagement.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vector.StockManagement.auth.AuthenticationController;

import java.util.ArrayList;
import java.util.List;

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
            List<String> whiteList = new ArrayList<>();
            whiteList.add("/api/tenants/");
            whiteList.add("/api/products/");
            whiteList.add("/api/auth/");
            whiteList.add("super_user/login");


            for (String uri : whiteList) {
                if (requestUri.startsWith(uri)) {
                    return;
                }
            }

        }


        config.enableTenantFilter();
    }
}
