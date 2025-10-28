package vector.StockManagement.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import vector.StockManagement.config.TenantContext;
//import vector.StockManagement.config.TenantTransactionSynchronization;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.PriceDisplayDTO;
import vector.StockManagement.model.dto.PriceOfEachSizeDTO;
import vector.StockManagement.model.dto.ProductDTO;
import vector.StockManagement.model.dto.ProductDisplayDTO;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.PriceListLevel;
import vector.StockManagement.model.enums.ProductCategory;
import vector.StockManagement.repositories.*;
import vector.StockManagement.services.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final ProductSizeRepository productSizeRepository;


    @Override
    public List<ProductDisplayDTO> findAll (PriceListLevel level) {
        List<ProductDisplayDTO> productDisplayDTOs = new ArrayList<>();

        if (level == PriceListLevel.DISTRIBUTOR){
            Long productId = null;
//            List<Order> orders = orderRepository.findAllByStatus(OrderStatus.FULFILLED);
//            for(Order order: orders) {
//                for(OrderLine orderLine: order.getOrderLines()) {
//                    productId = orderLine.getProduct().getId();
//                }
//            }
            List<Inventory> storeInventory = inventoryRepository.findAllByLocationType(LocationType.DISTRIBUTOR);
            for (Inventory inventory: storeInventory){
                productId = inventory.getProduct().getId();
            }

            for (Product  product: productRepository.findAll()) {
                if (Objects.equals(product.getId(), productId)) {
                    ProductDisplayDTO productDisplayDTO = getProductDisplayDTO(product);
                    productDisplayDTO.setPrice(getProductPrice(product, level));
                    productDisplayDTO.setQty(inventoryRepository.findByProductAndLocationType(product, LocationType.DISTRIBUTOR).getQtyAvailable());
                    productDisplayDTOs.add(productDisplayDTO);

                }

            }
            return productDisplayDTOs;
        }
        else {
            for (Product product: productRepository.findAll()) {
                if (inventoryRepository.findByProductAndLocationType(product, LocationType.WAREHOUSE )== null) {
                    continue;
                }

                ProductDisplayDTO dto = getProductDisplayDTO(product);
                dto.setPrice(getProductPrice(product, level));
                dto.setQty(inventoryRepository.findByProductAndLocationType(product, LocationType.WAREHOUSE).getQtyOnHand());
                productDisplayDTOs.add(dto);
            }
            return productDisplayDTOs;

        }

    }


    public Long getProductPrice(Product product, PriceListLevel level) {
        Long price = 0L;
        Long distributorPrice = null;
        Long factoryPrice = null;
        for(PriceList priceList: priceListRepository.findAll()) {

            if(priceList.getIsActive() == Boolean.TRUE) {
                for(PriceListItem item: priceList.getItems().stream().filter(p -> p.getProduct().getId().equals(product.getId())).toList()) {

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
//        productDisplayDTO.setSize(product.getSize());
        productDisplayDTO.setImageUrl(product.getImageUrl());
        productDisplayDTO.setFactoryPrice(product.getFactoryPrice());
        productDisplayDTO.setDistributorPrice(product.getDistributorPrice());
        productDisplayDTO.setProductCategory(product.getCategory());
        return productDisplayDTO;
    }

    @Override
    public ProductDisplayDTO findById1(Long id, PriceListLevel level) {
        Product product = productRepository.findById(id).orElse(null);
        ProductDisplayDTO dto = getProductDisplayDTO(product);
        if (level == PriceListLevel.DISTRIBUTOR){
            dto.setQty(inventoryRepository.findByProductAndLocationType(product, LocationType.DISTRIBUTOR).getQtyAvailable());
        }
        else {
            dto.setQty(inventoryRepository.findByProductAndLocationType(product, LocationType.WAREHOUSE).getQtyAvailable());
        }
        dto.setPrice(getProductPrice(product, level));
        Tenant tenant = tenantRepository.findById(TenantContext.getTenantId()).orElseThrow(() -> new RuntimeException("Tenant not found for this product"));
        dto.setTenantName(tenant.getName());
        return dto;
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product setPricesForEachSize(Product product, PriceOfEachSizeDTO dto){
        List<ProductSize> sizes = product.getSizes();
        Map<Long, Long> dtos = dto.getProductSizePrices();
        for (ProductSize size: sizes) {
            ProductSize productSize = productSizeRepository.findById(size.getId()).orElseThrow(() -> new RuntimeException("Product size not found for this product"));
            Long price = dtos.get(size.getId());
            productSize.setPrice(price);
        }
        return productRepository.save(product);
    }


    @Override
    @Transactional
    public Product save(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getProductName());
        product.setSku(productDTO.getSku());
        product.setCategory(ProductCategory.valueOf(productDTO.getCategory()));
        Tenant tenant = null;
        tenant = tenantRepository.findById(TenantContext.getTenantId()).orElseThrow(() -> new RuntimeException("Tenant not found"));
        product.setTenant(tenant);

        for (ProductDTO.ProductSize sizeDTO: productDTO.getProductSizes()){
            ProductSize productSize = new ProductSize();
            productSize.setSize(sizeDTO.getName());
            productSize.setPrice(sizeDTO.getPrice() != null? sizeDTO.getPrice(): product.getPrice());
            productRepository.save(product);
            productSize.setProduct(product);
            productSizeRepository.save(productSize);
            product.getSizes().add(productSize);

        }

        productRepository.saveAndFlush(product);


        return productRepository.saveAndFlush(product);
    }



    @Override
    public List<ProductDisplayDTO> getAllStoreProducts(){
        List<Product> products = productRepository.findAll();
        List<Product> storeProducts = new ArrayList<>();
        List<ProductDisplayDTO> productDisplayDTOs = new ArrayList<>();
        for (Product product : products) {
            if (inventoryRepository.findByProductAndLocationType(product, LocationType.DISTRIBUTOR )== null) {
                continue;
            }
            if (product == inventoryRepository.findByProductAndLocationType(product, LocationType.DISTRIBUTOR).getProduct()) {
                storeProducts.add(product);
            }
        }

        for(Product product: storeProducts){
            ProductDisplayDTO dto = getProductDisplayDTO(product);
            dto.setPrice(getProductPrice(product, PriceListLevel.DISTRIBUTOR));
            dto.setQty(inventoryRepository.findByProductAndLocationType(product, LocationType.DISTRIBUTOR).getQtyAvailable());
            productDisplayDTOs.add(dto);

        }
        return productDisplayDTOs;
    }



    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);// TODO: cascade price list items if needed
    }



    public PriceDisplayDTO getProductPrices(Long productId, Long tenantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        PriceDisplayDTO dto = new PriceDisplayDTO();
        List<PriceDisplayDTO.DistributorPriceDTO> distributorPriceDTOS = new ArrayList<>();
        List<PriceDisplayDTO.FactoryPriceDTO> factoryPriceDtOS = new ArrayList<>();
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setSku(product.getSku());


        for (PriceList priceList: priceListRepository.findAllByProduct(product)) {
            for(PriceListItem item : priceList.getItems()) {
                if (item.getPriceList().getLevel() == PriceListLevel.FACTORY) {
                    PriceDisplayDTO.FactoryPriceDTO factoryDTO = getFactoryDTO(item);
                    factoryPriceDtOS.add(factoryDTO);
                }
                else {
                    PriceDisplayDTO.DistributorPriceDTO distributorPriceDTO = getPriceDTO(item);
                    distributorPriceDTOS.add(distributorPriceDTO);
                }
            }
        }

        dto.setFactoryPrices(factoryPriceDtOS);
        dto.setDistributorPrices(distributorPriceDTOS);

        return dto;


    }

    private static PriceDisplayDTO.FactoryPriceDTO getFactoryDTO(PriceListItem item) {
        PriceDisplayDTO.FactoryPriceDTO factoryDTO = new PriceDisplayDTO.FactoryPriceDTO();
        factoryDTO.setBasePrice(item.getBasePrice());
        factoryDTO.setPriceListId(item.getPriceList().getId());
        factoryDTO.setValidFrom(item.getPriceList().getValidFrom());
        factoryDTO.setPriceListName(item.getPriceList().getName());
        factoryDTO.setActive(item.getPriceList().getIsActive());
        factoryDTO.setPriceListName(item.getPriceList().getName());
        return factoryDTO;
    }

    private static PriceDisplayDTO.DistributorPriceDTO getPriceDTO(PriceListItem item) {
        PriceDisplayDTO.DistributorPriceDTO distributorPriceDTO = new PriceDisplayDTO.DistributorPriceDTO();
        distributorPriceDTO.setBasePrice(item.getBasePrice());
        distributorPriceDTO.setPriceListId(item.getPriceList().getId());
        distributorPriceDTO.setValidFrom(item.getPriceList().getValidFrom());
        distributorPriceDTO.setPriceListName(item.getPriceList().getName());
        distributorPriceDTO.setActive(item.getPriceList().getIsActive());
        distributorPriceDTO.setBasePrice(item.getBasePrice());
        return distributorPriceDTO;
    }

    private static PriceDisplayDTO.DistributorPriceDTO getDistributorPriceDTO(PriceListItem distributorItem) {
        PriceDisplayDTO.DistributorPriceDTO distributorPrice = new PriceDisplayDTO.DistributorPriceDTO();
        distributorPrice.setPriceListId(distributorItem.getPriceList().getId());
        distributorPrice.setPriceListName(distributorItem.getPriceList().getName());
        distributorPrice.setBasePrice(distributorItem.getBasePrice());
        distributorPrice.setMinPrice(distributorItem.getMinPrice());
        distributorPrice.setValidFrom(distributorItem.getPriceList().getValidFrom());
        distributorPrice.setValidTo(distributorItem.getPriceList().getValidTo());
        distributorPrice.setActive(distributorItem.getPriceList().getIsActive());
        return distributorPrice;
    }

    private static PriceDisplayDTO.FactoryPriceDTO getFactoryPriceDTO(PriceListItem factoryItem) {
        PriceDisplayDTO.FactoryPriceDTO factoryPrice = new PriceDisplayDTO.FactoryPriceDTO();
        factoryPrice.setPriceListId(factoryItem.getPriceList().getId());
        factoryPrice.setPriceListName(factoryItem.getPriceList().getName());
        factoryPrice.setBasePrice(factoryItem.getBasePrice());
        factoryPrice.setMinPrice(factoryItem.getMinPrice());
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
//            if (product.getSize() != null) existing.setSize(product.getSize());
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
