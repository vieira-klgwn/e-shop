package vector.StockManagement.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.*;
import vector.StockManagement.model.dto.CreateSampleRequest;
import vector.StockManagement.model.dto.SampleResponse;
import vector.StockManagement.model.enums.LocationType;
import vector.StockManagement.model.enums.SampleStatus;
import vector.StockManagement.repositories.*;
import vector.StockManagement.services.InventoryService;
import vector.StockManagement.services.SampleService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {


    private final SampleRepository sampleRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final UserRepository userRepository;
    private final SampleItemRepository sampleItemRepository;
    private final OrderServiceImpl orderServiceImpl;
    private final ProductSizeRepository productSizeRepository;

    /// in the future, optimize this Sample to only consider that one product will be sent per sample.
    @Override
    public SampleResponse findById(Long id) {
        Sample sample = sampleRepository.findById(id).orElse(null);
        SampleResponse sampleResponse = new SampleResponse();
        if (sample.getProductId() != null) {
            Product product = productRepository.findById(sample.getProductId()).orElse(null);
            sampleResponse.setProductName(product != null ? product.getName() : "Unknown Product");
        } else {
            sampleResponse.setProductName("Unknown Product");
        }
        sampleResponse.setCreatedAt(sample.getCreatedAt());
        sampleResponse.setTotalItems(sample.getQuantity());// this is not serious bro
        sampleResponse.setId(sample.getId());
        sampleResponse.setDistributorFirstName(sample.getCustomer().getFirstName());
        sampleResponse.setDistributorLastName(sample.getCustomer().getLastName());
        sampleResponse.setSampleItems(sample.getItems());
        sampleResponse.setSampleStatus(sample.getStatus().toString());
        sampleResponse.setId(sample.getId());
        return sampleResponse;
    }

    @Override
    public List<SampleResponse> findAll() {
        List<Sample> samples= sampleRepository.findAll();
        List<SampleResponse> sampleResponses = new ArrayList<>();
        for (Sample sample : samples) {

            SampleResponse sampleResponse = new SampleResponse();
            if (sample.getProductId() != null) {
                Product product = productRepository.findById(sample.getProductId()).orElse(null);
                sampleResponse.setProductName(product != null ? product.getName() : "Unknown Product");
            } else {
                sampleResponse.setProductName("Unknown Product");
            }
            sampleResponse.setCreatedAt(sample.getCreatedAt());
            sampleResponse.setTotalItems(sample.getQuantity());// this is not serious bro
            sampleResponse.setDistributorFirstName(sample.getCustomer().getFirstName());
            sampleResponse.setDistributorLastName(sample.getCustomer().getLastName());
            sampleResponse.setSampleStatus(sample.getStatus().toString());
            sampleResponse.setId(sample.getId());
            sampleResponse.setSampleItems(sample.getItems());
            sampleResponses.add(sampleResponse);
            sampleResponse.setTotalItems(sample.getItems().size());


        }
        return sampleResponses;

    }

    @Override
    public Sample update(Long id, Sample sample) {
        Sample sample1 = sampleRepository.findById(id).get();
        sample.setItems(sample1.getItems());
        return sampleRepository.save(sample1);
    }

    @Override
    public Sample delete(Long id) {
        Sample sample = sampleRepository.findById(id).get();
        sampleRepository.delete(sample);
        return sample;
    }

    @Override
    @Transactional
    public Sample create(CreateSampleRequest request, User user) {

//        LocationType locationType = null;
//        if (user.getRole()== Role.SALES_MANAGER){
//             locationType= LocationType.WAREHOUSE;
//        }
//        else {
//            locationType= LocationType.DISTRIBUTOR;
//        }
        Sample sample = new Sample();


//
//        for (SampleItemDto item: request.getItems()){
//            Product product = productRepository.findById(item.getProductId()).get();
//            if (locationType == LocationType.WAREHOUSE){
//                inventoryService.transferStock(product, item.getQuantity(), LocationType.WAREHOUSE, LocationType.DISTRIBUTOR);
//            }
//            else {
//                inventoryService.transferStock(product, item.getQuantity(), LocationType.DISTRIBUTOR, LocationType.RETAILER);
//            }
//            SampleItem sampleItem = new SampleItem();
//            sampleItem.setQuantity(item.getQuantity());
//            sampleItem.setProduct(product);
//            sampleItemRepository.saveAndFlush(sampleItem);
//
//            sample.getItems().add(sampleItem);
//        }

        User customer = userRepository.findById(request.getCustomerId()).get();






        sample.setCreatedAt(LocalDateTime.now());
        sample.setNotes(request.getNotes());
        sample.setCustomer(customer);
        sample.setTenantId(user.getTenant().getId());
        sample.setStatus(SampleStatus.PENDING);



        request.getItems().forEach(item -> {

            item.getSizes().forEach((key, value) -> {
                Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
                updateProductBySizeAfterOrder(product, key, value);
                ProductSize size = productSizeRepository.findByProductAndSize(product, key);
                SampleItem sampleItem = new SampleItem();
                sampleItem.setProduct(product);
                sampleRepository.save(sample);
                sampleItem.setSample(sample);
                sampleItemRepository.save(sampleItem);
                size.setSampleItem(sampleItem);
                productSizeRepository.save(size);
                sample.getItems().add(sampleItem);

            });
        });



        return sampleRepository.save(sample);


    }

    private ProductSize updateProductBySizeAfterOrder(Product product, String productSize, Integer quantity) {

        ProductSize size = productSizeRepository.findByProductAndSize(product, productSize);
        if (size == null || size.getQuantityInStock() < quantity) {
            throw new RuntimeException("Insufficient stock for " + product.getName() + " with size " + productSize);
        }
        size.setQuantityInStock(size.getQuantityInStock()- quantity);
        return productSizeRepository.save(size);


    }

    @Override
    public Sample fullfillSample(Sample sample){
//        LocationType locationType = LocationType.WAREHOUSE; //explictly set it to warehouse but can change it when they want us to add functionality for distributor to also send samples
//        for (SampleItem item: sample.getItems()){
//            Product product = productRepository.findById(item.getProductId()).get();
//            if (locationType == LocationType.WAREHOUSE){
//                inventoryService.transferStock(product, item.getQuantity(), LocationType.WAREHOUSE, LocationType.DISTRIBUTOR);
//            }
//            else {
//                inventoryService.transferStock(product, item.getQuantity(), LocationType.DISTRIBUTOR, LocationType.RETAILER);
//            }
//
        Product product = null;
        if (sample.getProductId() != null) {
            product = productRepository.findById(sample.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

        }

        inventoryService.transferStock(product, sample.getQuantity(), LocationType.WAREHOUSE, LocationType.DISTRIBUTOR);
        sample.setStatus(SampleStatus.DELIVERED);
        return sampleRepository.save(sample);
    }


}
