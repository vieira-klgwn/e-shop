package vector.StockManagement.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vector.StockManagement.model.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class TenantFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    // Whitelisted endpoints that don't require tenant context
    private static final List<String> WHITELIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/forgot-password",
            "/api/auth/request-password-reset",
            "/api/auth/reset-password",
            "/api/tenants/admin",
            "/error",
            "/v3/api-docs",
            "/swagger-ui",
            "/actuator"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // Skip filter for whitelisted endpoints
        if (isWhitelisted(requestURI)) {
            logger.debug("Skipping tenant filter for whitelisted URI: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
                if (auth.getPrincipal() instanceof User user) {
                    if (user.getTenant() != null) {
                        TenantContext.setTenantId(user.getTenant().getId());
                        logger.info("Set tenant context from User principal to ID: {}", user.getTenant().getId());
                    } else if (user.getRole().name().equals("SUPER_ADMIN")) {
                        // SUPER_ADMIN gets global access with tenant ID 0
                        TenantContext.setTenantId(0L);
                        logger.info("Set tenant context to 0L for SUPER_ADMIN from User principal");
                    } else {
                        logger.warn("User {} has no tenant but is not SUPER_ADMIN", user.getEmail());
                    }
                } else {
                    logger.debug("Principal is not a User instance: {}", auth.getPrincipal().getClass());
                }
            } else {
                logger.debug("No valid authentication found, tenant context will remain unset");
            }

            chain.doFilter(request, response);
        } finally {
            // Note: Don't clear context here as JwtAuthenticationFilter will handle it
            logger.debug("TenantFilter completed for URI: {}", requestURI);
        }
    }

    private boolean isWhitelisted(String requestURI) {
        return WHITELIST.stream().anyMatch(requestURI::equals);
    }
}
