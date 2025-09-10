package vector.StockManagement.model.enums;



import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.*;
import java.util.stream.Collectors;


import static vector.StockManagement.model.enums.Permission.*;


@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(
            Set.of(ADMIN_READ, ADMIN_CREATE, ADMIN_UPDATE, ADMIN_DELETE,
                    MANAGER_READ, MANAGER_DELETE, MANAGER_UPDATE, MANAGER_CREATE,
                    DISTRIBUTOR_READ, DISTRIBUTOR_CREATE, DISTRIBUTOR_ORDER,
                    SALES_MANAGER_READ, SALES_MANAGER_CREATE, SALES_MANAGER_UPDATE, SALES_MANAGER_PRICING, SALES_MANAGER_PRODUCTS,
                    STORE_MANAGER_READ, STORE_MANAGER_CREATE, STORE_MANAGER_UPDATE, STORE_MANAGER_INVENTORY, STORE_MANAGER_RECEIVING,
                    WAREHOUSE_MANAGER_READ, WAREHOUSE_MANAGER_CREATE, WAREHOUSE_MANAGER_UPDATE, WAREHOUSE_MANAGER_INVENTORY,
                    ACCOUNTANT_READ, ACCOUNTANT_CREATE, ACCOUNTANT_UPDATE, ACCOUNTANT_INVOICES, ACCOUNTANT_PAYMENTS, ACCOUNTANT_REPORTS, ACCOUNTANT_CLOSE
            )
    ),
    MANAGER(
            Set.of(MANAGER_READ, MANAGER_CREATE, MANAGER_UPDATE,
                    DISTRIBUTOR_READ, SALES_MANAGER_READ, STORE_MANAGER_READ, WAREHOUSE_MANAGER_READ, ACCOUNTANT_READ
            )
    ),
    DISTRIBUTOR(
            Set.of(DISTRIBUTOR_READ, DISTRIBUTOR_CREATE, DISTRIBUTOR_ORDER
            )
    ),
    ACCOUNTANT(
            Set.of(ACCOUNTANT_READ, ACCOUNTANT_CREATE, ACCOUNTANT_UPDATE, 
                    ACCOUNTANT_INVOICES, ACCOUNTANT_PAYMENTS, ACCOUNTANT_REPORTS, ACCOUNTANT_CLOSE
            )
    ),
    SALES_MANAGER(
            Set.of(SALES_MANAGER_READ, SALES_MANAGER_CREATE, SALES_MANAGER_UPDATE, 
                    SALES_MANAGER_PRICING, SALES_MANAGER_PRODUCTS
            )
    ),
    STORE_MANAGER(
            Set.of(STORE_MANAGER_READ, STORE_MANAGER_CREATE, STORE_MANAGER_UPDATE, 
                    STORE_MANAGER_INVENTORY, STORE_MANAGER_RECEIVING
            )
    ),
    WAREHOUSE_MANAGER(
            Set.of(WAREHOUSE_MANAGER_READ, WAREHOUSE_MANAGER_CREATE, WAREHOUSE_MANAGER_UPDATE, 
                    WAREHOUSE_MANAGER_INVENTORY
            )
    );

    @Getter
    private final Set<Permission> permissions;



    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + name()));
        return authorities;
    }


}
