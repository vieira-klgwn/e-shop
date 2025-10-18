package vector.StockManagement.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Sample;
import vector.StockManagement.model.StockTransaction;
import vector.StockManagement.repositories.SampleRepository;
import vector.StockManagement.services.SampleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {


    private final SampleRepository sampleRepository;

    @Override
    public Sample findById(Long id) {
        return sampleRepository.findById(id).get();
    }

    @Override
    public List<Sample> findAll() {
        return sampleRepository.findAll();
    }

    @Override
    public Sample update(Long id, Sample sample) {
        Sample sample1 = sampleRepository.findById(id).get();
        sample.setQuantity(sample.getQuantity() + 1);
        return sampleRepository.save(sample1);
    }

    @Override
    public Sample delete(Long id) {
        Sample sample = sampleRepository.findById(id).get();
        sampleRepository.delete(sample);
        return sample;
    }

    @Override
    public Sample create(Sample sample) {


        return sampleRepository.save(sample);
    }


}
