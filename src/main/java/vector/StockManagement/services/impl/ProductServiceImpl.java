package vector.StockManagement.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
//import vector.StockManagement.config.TenantContext;
//import vector.StockManagement.config.TenantTransactionSynchronization;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.controllers.TestErrorController;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.PriceDisplayDTO;
import vector.StockManagement.model.dto.ProductDisplayDTO;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.PriceListLevel;
import vector.StockManagement.repositories.*;
import vector.StockManagement.services.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PriceListItemRepository priceListItemRepository;
    private final TenantRepository tenantRepository;
    private final InventoryRepository inventoryRepository;
    private final PriceListRepository priceListRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final OrderRepository orderRepository;


    @Override
    public List<ProductDisplayDTO> findAll (PriceListLevel level) {
        List<ProductDisplayDTO> productDisplayDTOs = new ArrayList<>();

        if (level == PriceListLevel.DISTRIBUTOR){
            Long productId = null;
            List<Order> orders = orderRepository.findAll();
            for(Order order: orders) {
                for(OrderLine orderLine: order.getOrderLines()) {
                    productId = orderLine.getProduct().getId();
                }
            }

            for (Product  product: productRepository.findAll()) {
                if (Objects.equals(product.getId(), productId)) {
                    ProductDisplayDTO productDisplayDTO = getProductDisplayDTO(product);
                    productDisplayDTO.setPrice(getProductPrice(product, level));
                    productDisplayDTOs.add(productDisplayDTO);

                }

            }
            return productDisplayDTOs;
        }
        else {
            for (Product product: productRepository.findAll()) {

                ProductDisplayDTO dto = getProductDisplayDTO(product);
                dto.setPrice(getProductPrice(product, level));
                productDisplayDTOs.add(dto);
            }
            return productDisplayDTOs;

        }

    }

    private Long getProductPrice(Product product, PriceListLevel level) {
        Long price = 0L;
        Long distributorPrice = null;
        Long factoryPrice = null;
        for(PriceList priceList: priceListRepository.findAll()) {

            if(priceList.getIsActive() == Boolean.TRUE) {
                for(PriceListItem item: priceList.getItems()) {

                    if (item.getPriceList().getLevel() == PriceListLevel.DISTRIBUTOR) {
                        distributorPrice = item.getBasePrice();
                    }
                    else if (item.getPriceList().getLevel() == PriceListLevel.FACTORY) {
                        factoryPrice = item.getBasePrice();
                    }
                }
            }
            else {
                return price;
            }
        }
        if (level == PriceListLevel.DISTRIBUTOR) {
            return distributorPrice;
        }
        else if (level == PriceListLevel.FACTORY) {
            return factoryPrice;
        }
        else {
            return null;
        }

    }



    private static ProductDisplayDTO getProductDisplayDTO(Product product) {
        ProductDisplayDTO productDisplayDTO = new ProductDisplayDTO();
        productDisplayDTO.setId(product.getId());
        productDisplayDTO.setName(product.getName());
        productDisplayDTO.setDescription(product.getDescription());
        productDisplayDTO.setCategory(String.valueOf(product.getCategory()));
        productDisplayDTO.setSize(product.getSize());
        productDisplayDTO.setImageUrl(product.getImageUrl());
        return productDisplayDTO;
    }

    @Override
    public ProductDisplayDTO findById1(Long id, PriceListLevel level) {
        Product product = productRepository.findById(id).orElse(null);
        ProductDisplayDTO dto = getProductDisplayDTO(product);
        dto.setPrice(getProductPrice(product, level));
        return dto;
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Product save(Product product) {

        Tenant tenant = null;

        tenant = tenantRepository.findById(TenantContext.getTenantId()).orElseThrow(() -> new RuntimeException("Tenant not found"));
        product.setTenant(tenant);
        productRepository.saveAndFlush(product);
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setLocationType(LocationType.WAREHOUSE);
        inventory.setLocationId(12L);
        inventory.setTenant(tenant);
        inventory.setQtyOnHand(0);
        inventoryRepository.saveAndFlush(inventory);
        return productRepository.saveAndFlush(product);
    }

    @Override
    public List<Product> getAllStoreProducts(){
        List<Product> products = productRepository.findAll();
        List<Product> storeProducts = new ArrayList<>();
        for (Product product : products) {
            if(inventoryRepository.findByProduct(product).getLocationType() == LocationType.DISTRIBUTOR){
                storeProducts.add(product);
            }
        }

        return storeProducts;
    }



    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);// TODO: cascade price list items if needed
    }



    public PriceDisplayDTO getProductPrices(Long productId, Long tenantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        PriceDisplayDTO dto = new PriceDisplayDTO();
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setSku(product.getSku());

        // Fetch FACTORY price
        PriceListItem factoryItem = priceListItemRepository.findByProductIdAndTenantId(productId, tenantId)
                .stream()
                .filter(item -> item.getPriceList().getLevel() == PriceListLevel.FACTORY).findFirst().orElse(null);
        //                        && item.getPriceList().isValidForDate(LocalDate.now()))
////                .findFirst()
//                .orElse(null);
        if (factoryItem != null) {
            PriceDisplayDTO.FactoryPriceDTO factoryPrice = getFactoryPriceDTO(factoryItem);
            factoryPrice.setActive(factoryItem.getPriceList().getIsActive());

            dto.setFactoryPrice(factoryPrice);
        }

        // Fetch DISTRIBUTOR price
        PriceListItem distributorItem = priceListItemRepository.findByProductIdAndTenantId(productId, tenantId)
                .stream()
                .filter(item -> item.getPriceList().getLevel() == PriceListLevel.DISTRIBUTOR).findFirst().orElse(null);
//                        && item.getPriceList().isValidForDate(LocalDate.now()))
////                .findFirst()
//                .orElse(null);
        if (distributorItem != null) {
            PriceDisplayDTO.DistributorPriceDTO distributorPrice = getDistributorPriceDTO(distributorItem);
            dto.setDistributorPrice(distributorPrice);
        }

        return dto;
    }

    private static PriceDisplayDTO.DistributorPriceDTO getDistributorPriceDTO(PriceListItem distributorItem) {
        PriceDisplayDTO.DistributorPriceDTO distributorPrice = new PriceDisplayDTO.DistributorPriceDTO();
        distributorPrice.setPriceListId(distributorItem.getPriceList().getId());
        distributorPrice.setPriceListName(distributorItem.getPriceList().getName());
        distributorPrice.setBasePrice(BigDecimal.valueOf(distributorItem.getBasePrice()));
        distributorPrice.setMinPrice(BigDecimal.valueOf(distributorItem.getMinPrice()));
        distributorPrice.setValidFrom(distributorItem.getPriceList().getValidFrom());
        distributorPrice.setValidTo(distributorItem.getPriceList().getValidTo());
        distributorPrice.setActive(distributorItem.getPriceList().getIsActive());
        return distributorPrice;
    }

    private static PriceDisplayDTO.FactoryPriceDTO getFactoryPriceDTO(PriceListItem factoryItem) {
        PriceDisplayDTO.FactoryPriceDTO factoryPrice = new PriceDisplayDTO.FactoryPriceDTO();
        factoryPrice.setPriceListId(factoryItem.getPriceList().getId());
        factoryPrice.setPriceListName(factoryItem.getPriceList().getName());
        factoryPrice.setBasePrice(BigDecimal.valueOf(factoryItem.getBasePrice()));
        factoryPrice.setMinPrice(BigDecimal.valueOf(factoryItem.getMinPrice()));
        factoryPrice.setValidFrom(factoryItem.getPriceList().getValidFrom());
        factoryPrice.setValidTo(factoryItem.getPriceList().getValidTo());
        return factoryPrice;
    }

    @Override
    public Product update(Long id, Product product) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing != null) {
            if (product.getName() != null) existing.setName(product.getName());
            if (product.getSku() != null) existing.setSku(product.getSku());
            if (product.getDescription() != null) existing.setDescription(product.getDescription());
            if (product.getPrice() != null) existing.setPrice(product.getPrice());
            if (product.getUnit() != 0) existing.setUnit(product.getUnit());
            if (product.getSize() != null) existing.setSize(product.getSize());
            if (product.getCode() != null) existing.setCode(product.getCode());
            if (product.getCategory() != null) existing.setCategory(product.getCategory());
            if (product.getAttributes() != null) existing.setAttributes(product.getAttributes());
            if (product.getIsActive() != null) existing.setIsActive(product.getIsActive());
            if (product.getBarcode() != null) existing.setBarcode(product.getBarcode());
            if (product.getWeight() != null) existing.setWeight(product.getWeight());
            if (product.getImageUrl() != null) existing.setImageUrl(product.getImageUrl());
            if (product.getWarehouse() != null) existing.setWarehouse(product.getWarehouse());
            return productRepository.save(existing);
        }
        return null;

    }
}
