package vector.StockManagement.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vector.StockManagement.model.Distributor;
import vector.StockManagement.repositories.DistributorRepository;
import vector.StockManagement.services.DistributorService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistributorServiceImpl implements DistributorService {

    private final DistributorRepository distributorRepository;

    @Override
    public List<Distributor> findAll() {
        return distributorRepository.findAll();
    }

    @Override
    public Distributor findById(Long id) {
        return distributorRepository.findById(id).orElse(null);
    }

    @Override
    public Distributor save(Distributor distributor) {
        return distributorRepository.save(distributor);
    }

    @Override
    public void delete(Long id) {
        distributorRepository.deleteById(id);
    }
}