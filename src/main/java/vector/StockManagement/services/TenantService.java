package vector.StockManagement.services;



import vector.StockManagement.model.Tenant;

import java.util.List;

public interface TenantService {
    List<Tenant> findAll();
    Tenant findById(Long id);
    Tenant save(Tenant tenant);
    void delete(Long id);
}