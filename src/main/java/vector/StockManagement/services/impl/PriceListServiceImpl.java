package vector.StockManagement.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.PriceList;
import vector.StockManagement.model.Tenant;
import vector.StockManagement.model.User;
import vector.StockManagement.repositories.PriceListRepository;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.services.PriceListService;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PriceListServiceImpl implements PriceListService {

    private final PriceListRepository priceListRepository;
    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;

    @Override
    public List<PriceList> findAll() {
        return priceListRepository.findAll();
    }

    @Override
    public PriceList findById(Long id) {
        return priceListRepository.findById(id).orElse(null);
    }

    @Override
    public PriceList save(PriceList priceList) {



        priceList.getItems().forEach(item -> {
            item.getProduct().setPrice(item.getBasePrice());
            priceList.setProduct(item.getProduct());
        });

        priceList.setTenant(tenantRepository.findById(TenantContext.getTenantId()).orElse(null));
        priceList.setIsActive(true);







        return priceListRepository.saveAndFlush(priceList);
    }



    @Override
    public void delete(Long id) {
        priceListRepository.deleteById(id);
    }
}