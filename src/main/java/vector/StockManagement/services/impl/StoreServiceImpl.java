package vector.StockManagement.services.impl;



import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ColumnTransformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vector.StockManagement.auth.AuthenticationResponse;
import vector.StockManagement.auth.AuthenticationService;
import vector.StockManagement.auth.RegisterRequest;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.StoreDTO;
import vector.StockManagement.model.enums.Role;
import vector.StockManagement.repositories.DistributorRepository;
import vector.StockManagement.repositories.StoreRepository;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.repositories.UserRepository;
import vector.StockManagement.services.StoreService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final DistributorRepository distributorRepository;
    private final TenantRepository tenantRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Override
    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    @Override
    public Store findById(Long id) {
        return storeRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public AuthenticationResponse save(StoreDTO storeDTO) {

        Tenant tenant = null;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            tenant = user.getTenant();

        }


        RegisterRequest registerRequest = getRegisterRequest(storeDTO, tenant);
        AuthenticationResponse response = authenticationService.register(registerRequest);

        User manager = userRepository.findByEmail(storeDTO.getManagerEmail()).orElseThrow(()-> new RuntimeException("Manager not found after registration"));



        Store newStore = new Store();
        newStore.setName(storeDTO.getName());
        newStore.setCode(storeDTO.getCode());
        newStore.setAddress(storeDTO.getAddress());
        newStore.setRegion(storeDTO.getRegion());
        newStore.setTenant(tenant);
        newStore.setManager(manager);
        storeRepository.save(newStore);

        return response;


    }

    private static RegisterRequest getRegisterRequest(StoreDTO storeDTO, Tenant tenant) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(storeDTO.getManagerFirstName());
        registerRequest.setLastName(storeDTO.getManagerLastName());
        registerRequest.setEmail(storeDTO.getManagerEmail());
        registerRequest.setGender(storeDTO.getManagerGender());
        registerRequest.setPassword(storeDTO.getManagerPassword());
        registerRequest.setConfirmPassword(storeDTO.getManagerConfirmPassword());
        registerRequest.setRole(Role.STORE_MANAGER);
        registerRequest.setTenant(tenant);
        return registerRequest;
    }

    @Override
    public Store update(Long id,Store store) {
        Store existing = storeRepository.findById(id).orElseThrow(()-> new RuntimeException("Store not found after update"));

        if (existing != null) {
            existing.setName(store.getName());
            existing.setCode(store.getCode());
            existing.setAddress(store.getAddress());
            existing.setRegion(store.getRegion());
            return storeRepository.save(existing);
        }
        else {
            return null;
        }

    }



    @Override
    public void delete(Long id) {
        storeRepository.deleteById(id);
    }
}