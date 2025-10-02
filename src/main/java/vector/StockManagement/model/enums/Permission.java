package vector.StockManagement.model.enums;



import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_CREATE("admin:create"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),
    
    MANAGER_READ("manager:read"),
    MANAGER_DELETE("manager:delete"),
    MANAGER_UPDATE("manager:update"),
    MANAGER_CREATE("manager:create"),
    
    DISTRIBUTOR_READ("distributor:read"),
    DISTRIBUTOR_CREATE("distributor:create"),
    DISTRIBUTOR_ORDER("distributor:order"),
    
    SALES_MANAGER_READ("sales_manager:read"),
    SALES_MANAGER_CREATE("sales_manager:create"),
    SALES_MANAGER_UPDATE("sales_manager:update"),
    SALES_MANAGER_PRICING("sales_manager:pricing"),
    SALES_MANAGER_PRODUCTS("sales_manager:products"),
    
    STORE_MANAGER_READ("store_manager:read"),
    STORE_MANAGER_CREATE("store_manager:create"),
    STORE_MANAGER_UPDATE("store_manager:update"),
    STORE_MANAGER_INVENTORY("store_manager:inventory"),
    STORE_MANAGER_RECEIVING("store_manager:receiving"),
    
    WAREHOUSE_MANAGER_READ("warehouse_manager:read"),
    WAREHOUSE_MANAGER_CREATE("warehouse_manager:create"),
    WAREHOUSE_MANAGER_UPDATE("warehouse_manager:update"),
    WAREHOUSE_MANAGER_INVENTORY("warehouse_manager:inventory"),
    
    ACCOUNTANT_READ("accountant:read"),
    ACCOUNTANT_CREATE("accountant:create"),
    ACCOUNTANT_UPDATE("accountant:update"),
    ACCOUNTANT_INVOICES("accountant:invoices"),
    ACCOUNTANT_PAYMENTS("accountant:payments"),
    ACCOUNTANT_REPORTS("accountant:reports"),
    ACCOUNTANT_CLOSE("accountant:close"),

    REPORT_READ("report:read"),

    TENANT_CREATE("tenant:create"),
    TENANT_READ("tenant:read"),
    TENANT_UPDATE("tenant:update"),
    TENANT_DELETE("tenant:delete"),

    RETAILER_ORDER("retailer:order"),

    STORE_ACCOUNTANT("store:accountant");


    @Getter
    private final String permission;

}


