package vector.StockManagement.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vector.StockManagement.model.User;

import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // Skip filter for /api/tenants/admin
        if ("/api/tenants/admin".equals(requestURI) || requestURI.startsWith("/api/auth/") || requestURI.contains("super_user/login") || requestURI.contains("api/tenants")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof User user && user.getTenant() != null) {
                TenantContext.setTenantId(user.getTenant().getId());
            }
            chain.doFilter(request, response);
        } finally {
//            TenantContext.clear();
            System.out.println("Tenant filter executed");

        }
    }
}
