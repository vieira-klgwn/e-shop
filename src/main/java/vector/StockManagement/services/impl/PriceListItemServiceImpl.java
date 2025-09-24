package vector.StockManagement.services.impl;



import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.PriceList;
import vector.StockManagement.model.PriceListItem;
import vector.StockManagement.model.Product;
import vector.StockManagement.model.dto.PriceListItemDTO;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.PriceListLevel;
import vector.StockManagement.model.enums.ProductCategory;
import vector.StockManagement.repositories.PriceListItemRepository;
import vector.StockManagement.repositories.PriceListRepository;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.services.PriceListItemService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PriceListItemServiceImpl implements PriceListItemService {

    private final PriceListItemRepository priceListItemRepository;
    private final ProductRepository productRepository;
    private final PriceListRepository priceListRepository;
    private final TenantRepository tenantRepository;

    @Override
    public List<PriceListItem> findAll() {
        return priceListItemRepository.findAll();
    }

    @Override
    public PriceListItem findById(Long id) {
        return priceListItemRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public PriceListItem save(PriceListItemDTO priceListItemDTO) {
        Product product = productRepository.findById(priceListItemDTO.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

        PriceList priceList = priceListRepository.findById(priceListItemDTO.getPriceListId()).orElseThrow(() -> new RuntimeException("Price list not found"));

        PriceListItem priceListItem = new PriceListItem();
        priceListItem.setProduct(product);
        priceListItem.setPriceList(priceList);
        priceListItem.setBasePrice(priceListItemDTO.getBasePrice());
        priceListItem.setMinPrice(priceListItemDTO.getMinPrice());
        priceListItem.setTenant(tenantRepository.findById(priceListItemDTO.getTenantId()).orElseThrow(() -> new RuntimeException("Tenant not found")));
        priceListItemRepository.saveAndFlush(priceListItem);
        product.setPrice(priceListItem.getBasePrice());
        productRepository.saveAndFlush(product);

        priceListRepository.saveAndFlush(priceList);
        priceList.getItems().add(priceListItem);
        return priceListItemRepository.save(priceListItem);
    }

    @Override
    public void delete(Long id) {
        priceListItemRepository.deleteById(id);
    }
}