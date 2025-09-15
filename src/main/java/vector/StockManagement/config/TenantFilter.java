package vector.StockManagement.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vector.StockManagement.model.User;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TenantFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();


        // Skip filter for /api/tenants/admin
        List<String> whiteList = new ArrayList<>();
        whiteList.add("/api/tenants/");
        whiteList.add("/api/products/");
        whiteList.add("/api/auth/");
        whiteList.add("super_user/login");
        for (String uri : whiteList) {
            if (requestURI.startsWith(uri)) {
                chain.doFilter(request, response);
                return;
            }
        }


        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                if (auth instanceof AnonymousAuthenticationToken) {
                    logger.debug("Anonymous user detected; skipping tenant context setting");
                } else if (auth.getPrincipal() instanceof User user && user.getTenant() != null) {
                    TenantContext.setTenantId(user.getTenant().getId());
                    logger.info("Set tenant context to ID: {}", user.getTenant().getId());
                } else {
                    logger.warn("Authenticated user has no tenant; setting default tenant ID: 0");
                    TenantContext.setTenantId(Long.parseLong("19")); // Fallback for SUPER_ADMIN
                }
            } else {
                logger.debug("No authentication present; skipping tenant context setting");
            }

            chain.doFilter(request, response);
        } finally {
//            TenantContext.clear();
            System.out.println("Tenant filter eAuthenticationControllerxecuted");

        }
    }
}
