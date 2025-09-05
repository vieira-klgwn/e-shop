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
import vector.StockManagement.services.PriceListItemService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PriceListItemServiceImpl implements PriceListItemService {

    private final PriceListItemRepository priceListItemRepository;
    private final ProductRepository productRepository;
    private final PriceListRepository priceListRepository;

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
        Product product = new Product();
        PriceList priceList = new PriceList();
        product.setUnit(5);
        product.setName("sample_product");
        product.setSku("000");
        product.setCode("000");
        product.setPrice(Long.parseLong("500"));
        product.setImageUrl("imageUrl");
        product.setDescription("description");
        product.setCategory(ProductCategory.ACTIVEWEAR_PANTS);
        product.setSize("200g");
        priceList.setName("priceList");
        priceList.setDescription("description");
        priceList.setLevel(PriceListLevel.FACTORY);
        priceList.setCurrency("frw");
        priceList.setValidFrom(LocalDate.now());
        priceList.setValidTo(LocalDate.now().plusDays(100));

        priceListRepository.save(priceList);
        productRepository.save(product);

        PriceListItem priceListItem = new PriceListItem();
        priceListItem.setProduct(product);
        priceListItem.setPriceList(priceList);
        priceListItem.setBasePrice(priceListItemDTO.getBasePrice());
        priceListItem.setMinPrice(priceListItemDTO.getMinPrice());

        return priceListItemRepository.save(priceListItem);
    }

    @Override
    public void delete(Long id) {
        priceListItemRepository.deleteById(id);
    }
}