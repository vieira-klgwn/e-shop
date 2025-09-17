package vector.StockManagement.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
//import vector.StockManagement.config.TenantContext;
//import vector.StockManagement.config.TenantTransactionSynchronization;
import vector.StockManagement.config.TenantContext;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.PriceDisplayDTO;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.PriceListLevel;
import vector.StockManagement.repositories.InventoryRepository;
import vector.StockManagement.repositories.PriceListItemRepository;
import vector.StockManagement.repositories.ProductRepository;
import vector.StockManagement.repositories.TenantRepository;
import vector.StockManagement.services.ProductService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PriceListItemRepository priceListItemRepository;
    private final TenantRepository tenantRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
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
        inventory.setLocationType(LocationType.L1);
        inventory.setLocationId(12L);
        inventory.setTenant(tenant);
        inventory.setQtyOnHand(0);
        inventoryRepository.saveAndFlush(inventory);
        return productRepository.saveAndFlush(product);
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
