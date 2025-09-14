package vector.StockManagement.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vector.StockManagement.model.User;


import java.io.IOException;

@Component
public class TenantFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();


        // Skip filter for /api/tenants/admin
        if ("/api/tenants/admin".equals(requestURI) || requestURI.startsWith("/api/auth/") || requestURI.contains("super_user/login") || requestURI.contains("api/tenants") || requestURI.contains("api/users")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();


            logger.info("Authenticated user: {}", auth);
            logger.info("Authenticated user: {}", auth.getName());
            logger.info("Authenticated user: {}", auth.getPrincipal());
            logger.info("Authenticated user: {}", auth.getDetails());
            logger.info("Authenticated user: {}", auth.getAuthorities());
            logger.info("Authenticated user: {}", auth.getCredentials());


            if (auth != null && auth.getPrincipal() instanceof User user && user.getTenant() != null) {
                TenantContext.setTenantId(user.getTenant().getId());
                logger.info("Authenticated user: {}", user.getTenant().getId());
            }
            chain.doFilter(request, response);
        } finally {
//            TenantContext.clear();
            System.out.println("Tenant filter eAuthenticationControllerxecuted");

        }
    }
}
