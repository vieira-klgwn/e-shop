package vector.StockManagement.services.impl;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.*;
import vector.StockManagement.repositories.DistributorRepository;
import vector.StockManagement.repositories.StoreRepository;
import vector.StockManagement.services.StoreService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final DistributorRepository distributorRepository;

    @Override
    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    @Override
    public Store findById(Long id) {
        return storeRepository.findById(id).orElse(null);
    }

    @Override
    public Store save(Store store) {
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            Tenant currentTenant = user.getTenant();
            store.setTenant(currentTenant);
            return storeRepository.save(store);
        }
        else {
            throw new IllegalStateException("Make sure this authenticated user is of type UserDetails");
        }

    }

    @Override
    public void delete(Long id) {
        storeRepository.deleteById(id);
    }
}