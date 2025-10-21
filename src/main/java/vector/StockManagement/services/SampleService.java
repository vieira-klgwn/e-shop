package vector.StockManagement.services;


import vector.StockManagement.model.Sample;
import vector.StockManagement.model.StockTransaction;
import vector.StockManagement.model.User;
import vector.StockManagement.model.dto.CreateSampleRequest;
import vector.StockManagement.model.dto.SampleResponse;
import vector.StockManagement.services.impl.SampleServiceImpl;

import java.util.List;

public interface SampleService {
    SampleResponse findById(Long id);
    List<SampleResponse> findAll();
    Sample update(Long id, Sample sample);
    Sample delete(Long id);
    Sample create(CreateSampleRequest sample, User user);

    Sample fullfillSample(Sample sample);
}
