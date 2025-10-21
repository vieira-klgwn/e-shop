package vector.StockManagement.services.impl;

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
        sampleResponse.setDistributorFirstName(sample.getDistributor().getFirstName());
        sampleResponse.setDistributorLastName(sample.getDistributor().getLastName());
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
            sampleResponse.setId(sample.getId());
            sampleResponse.setDistributorFirstName(sample.getDistributor().getFirstName());
            sampleResponse.setDistributorLastName(sample.getDistributor().getLastName());
            sampleResponse.setSampleStatus(sample.getStatus().toString());
            sampleResponse.setId(sample.getId());
            sampleResponses.add(sampleResponse);


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

        User distributor = userRepository.findById(request.getDistributorId()).get();



        sample.setQuantity(request.getQuantity());
        sample.setProductId(request.getProductId());

        sample.setCreatedAt(LocalDateTime.now());
        sample.setNotes(request.getNotes());
        sample.setDistributor(distributor);
        sample.setTenantId(user.getTenant().getId());
        sample.setStatus(SampleStatus.PENDING);
        return sampleRepository.save(sample);


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
