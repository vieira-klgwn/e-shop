package vector.StockManagement.services;


import vector.StockManagement.model.Sample;
import vector.StockManagement.model.StockTransaction;
import vector.StockManagement.services.impl.SampleServiceImpl;

import java.util.List;

public interface SampleService {
    Sample findById(Long id);
    List<Sample> findAll();
    Sample update(Long id, Sample sample);
    Sample delete(Long id);
    Sample create(Sample sample);

}
