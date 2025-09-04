package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.*;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.repositories.DistributorRepository;
import vector.StockManagement.repositories.WarehouseRepository;
import vector.StockManagement.services.WarehouseService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final DistributorRepository distributorRepository;

    @Override
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id).orElse(null);
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            Tenant currentTenant = user.getTenant();
            warehouse.setTenant(currentTenant);
            return warehouseRepository.save(warehouse);
        }
        else {
            throw new IllegalStateException("Make sure this authenticated user is of type UserDetails");
        }

    }

    @Override
    public void delete(Long id) {
        warehouseRepository.deleteById(id);
    }
}